package com.millennialmedia.intellibot.psi.element;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.jetbrains.python.psi.PyClass;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.psi.RobotFeatureFileType;
import com.millennialmedia.intellibot.psi.RobotLanguage;
import com.millennialmedia.intellibot.psi.RobotProjectData;
import com.millennialmedia.intellibot.psi.dto.ImportType;
import com.millennialmedia.intellibot.psi.dto.VariableDto;
import com.millennialmedia.intellibot.psi.ref.PythonResolver;
import com.millennialmedia.intellibot.psi.ref.RobotPythonClass;
import com.millennialmedia.intellibot.psi.util.PerformanceCollector;
import com.millennialmedia.intellibot.psi.util.PerformanceEntity;
import com.millennialmedia.intellibot.psi.util.ReservedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static com.millennialmedia.intellibot.psi.element.HeadingImpl.DEFAULT_RESOURCE_NAME;
import static com.millennialmedia.intellibot.psi.element.HeadingImpl.ROBOT_BUILT_IN;

/**
 * @author Stephen Abrams
 */
public class RobotFileImpl extends PsiFileBase implements RobotFile, KeywordFile, PerformanceEntity {

    private Collection<Heading> headings;

    public RobotFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, RobotLanguage.INSTANCE);
    }

    private static void debug(@NotNull String key, String data, @NotNull Project project) {
        if (RobotOptionsProvider.getInstance(project).isDebug()) {
            String message = String.format("[RobotFileImpl][%s] %s", key, data);
            Notifications.Bus.notify(new Notification("intellibot.debug", "Debug", message, NotificationType.INFORMATION));
        }
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return RobotFeatureFileType.getInstance();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.headings = null;
    }

    /* now getDefinedVariables include all variable in imported files, also see ResolverUtil.java and RobotCompletionContributor.java
     * ROBOTFRAMEWORK only import variable from Variable and Resource
     * to avoid recursive call, any method called by getDefinedVariables (transitively) should call getOwnDefinedVariables
     */
    @NotNull
    @Override
    public Collection<DefinedVariable> getDefinedVariables() {
        Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();
        if (! getContainingFile().getName().equals(DEFAULT_RESOURCE_NAME)) {
            addBuiltInVariables(results);
            addProjectDefaultVariables(results);
        }
        results.addAll(getOwnDefinedVariables());
        for (KeywordFile imported : getImportedFiles(-1)) {
            if (imported.getImportType() == ImportType.VARIABLES || imported.getImportType() == ImportType.RESOURCE) {
                results.addAll(imported.getOwnDefinedVariables());
            }
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getOwnDefinedVariables() {
        Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();
        for (Heading heading : getHeadings()) {
            results.addAll(heading.getDefinedVariables());
        }
        return results;
    }

    @NotNull
    @Override
    public ImportType getImportType() {
        return ImportType.RESOURCE;
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getDefinedKeywords() {
        Collection<DefinedKeyword> results = new LinkedHashSet<DefinedKeyword>();
        for (Heading heading : getHeadings()) {
            results.addAll(heading.getDefinedKeywords());
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables() {
        Collection<PsiFile> results = new HashSet<PsiFile>();
        for (Heading heading : getHeadings()) {
            results.addAll(heading.getFilesFromInvokedKeywordsAndVariables());
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<KeywordFile> getImportedFiles(int maxTransitiveDepth) {
        if (maxTransitiveDepth < 0) {
            maxTransitiveDepth = RobotOptionsProvider.getInstance(getProject()).maxTransitiveDepth();
        }
        if (maxTransitiveDepth == 0) {
            return Collections.emptyList();
        }

        Collection<KeywordFile> results = new LinkedHashSet<KeywordFile>();
        if (! getContainingFile().getName().equals(DEFAULT_RESOURCE_NAME)) {
            addBuiltInImports(results);
        }
        for (Heading heading : getHeadings()) {
            for (KeywordFile file : heading.getImportedFiles()) {
                addKeywordFiles(results, file, maxTransitiveDepth - 1);
            }
        }
        return results;
    }

    private void addKeywordFiles(Collection<KeywordFile> files, KeywordFile current, int maxTransitiveDepth) {
        files.add(current);
        if (maxTransitiveDepth > 0) {
            for (KeywordFile file : current.getImportedFiles(1)) {
                addKeywordFiles(files, file, maxTransitiveDepth - 1);
            }
        }
    }

    @Override
    public void importsChanged() {
        for (Heading heading : getHeadings()) {
            heading.importsChanged();
        }
    }

    @NotNull
    @Override
    public Collection<KeywordInvokable> getKeywordReferences(@Nullable KeywordDefinition definition) {
        Collection<KeywordInvokable> results = new LinkedHashSet<KeywordInvokable>();
        for (Heading heading : getHeadings()) {
            results.addAll(heading.getKeywordReferences(definition));
        }
        return results;
    }

    @NotNull
    private Collection<Heading> getHeadings() {
        Collection<Heading> results = this.headings;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "headings");
            results = collectHeadings();
            this.headings = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<Heading> collectHeadings() {
        Collection<Heading> results = new LinkedHashSet<Heading>();
        for (PsiElement child : getChildren()) {
            if (child instanceof Heading) {
                results.add((Heading) child);
            }
        }
        return results;
    }

    @NotNull
    @Override
    public String getDebugFileName() {
        return getVirtualFile().getName();
    }

    @NotNull
    @Override
    public String getDebugText() {
        return ".";
    }

    private void addBuiltInVariables(@NotNull Collection<DefinedVariable> variables) {
        variables.addAll(getBuiltInVariables());
    }

    // TODO: code highlight is not quite working; see KyleEtlPubAdPart.robot; think it has to do with name difference GLOBAL_VARIABLE vs CURDIR etc
    private synchronized Collection<DefinedVariable> getBuiltInVariables() {
        Collection<DefinedVariable> variables = RobotProjectData.getInstance(getProject()).builtInVariables();
        if (variables == null) {
            Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();

            PsiElement tmp = null;
            // optimized here: all variable.getVariable(getProject()) return same object,
            // so now only call variable.getVariable(getProject()) only once
            PsiElement pythonVariable = null;
            for (ReservedVariable variable : ReservedVariable.values()) {
                if (pythonVariable == null) {
                    pythonVariable = variable.getVariable(getProject());
                }
                if (pythonVariable != null) {
                    // already formatted ${X}
                    results.add(new VariableDto(pythonVariable, variable.getVariable(), variable.getScope()));
                }
            }

            variables = results.isEmpty() ?
                    Collections.<DefinedVariable>emptySet() :
                    Collections.unmodifiableCollection(results);
            RobotProjectData.getInstance(getProject()).setBuiltInVariables(variables);
        }

        return variables;
    }

    // bug?
    // Project A, open Project B (this windows), then open Project A (this windows)
    // The first time of call sequence like below:
    //     HeadlingImpl.collectVariables -> getImportedFiles -> collectImportFiles -> addBuiltInImports -> PythonResolver.findClass -> PythonResolver.safeFindClass -> PyClassNameIndex.find
    // will be hang, no return from PyClassNameIndex.find
    // But the second and subsequent call is successful.
    private void addProjectDefaultVariables(@NotNull Collection<DefinedVariable> variables) {
        variables.addAll(getProjectDefaultVariables());
    }

    // use service RobotProjectData to provide project instanced of default varaibles
    private synchronized Collection<DefinedVariable> getProjectDefaultVariables() {
        Project project = getProject();
        RobotProjectData robotProjectData = RobotProjectData.getInstance(project);
        if (! RobotOptionsProvider.getInstance(project).loadProjectDefaultVariable()) {
            robotProjectData.setProjectGlobalDefaultVariables(null);
            return Collections.<DefinedVariable>emptySet();
        }
        Collection<DefinedVariable> variables = robotProjectData.projectGlobalDefaultVariables();
        if (variables == null) {
            Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();
            PsiFile psiFile = null;
            String filename = project.getBasePath() + "/" + DEFAULT_RESOURCE_NAME;
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(filename));
            if (virtualFile != null) {
                psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                if (psiFile != null) {
                    results.addAll(new RobotFileImpl(psiFile.getViewProvider()).getDefinedVariables());
                }
            }
            if (psiFile == null) {
                debug("getProjectDefaultVariables", "File not found: "+filename, getProject());
            }
            variables = results.isEmpty() ?
                    Collections.<DefinedVariable>emptySet() :
                    Collections.unmodifiableCollection(results);
            robotProjectData.setProjectGlobalDefaultVariables(variables);
        }
        return variables;
    }

    private void addBuiltInImports(@NotNull Collection<KeywordFile> files) {
        PyClass builtIn = PythonResolver.findClass(ROBOT_BUILT_IN, getProject());
        if (builtIn != null) {
            files.add(new RobotPythonClass(ROBOT_BUILT_IN, ROBOT_BUILT_IN, builtIn, ImportType.LIBRARY));
        }
    }
}
