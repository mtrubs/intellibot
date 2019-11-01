package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.MultiMap;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.ide.icons.RobotIcons;
import com.millennialmedia.intellibot.psi.RobotProjectData;
import com.millennialmedia.intellibot.psi.dto.ImportType;
import com.millennialmedia.intellibot.psi.dto.VariableDto;
import com.millennialmedia.intellibot.psi.ref.PythonResolver;
import com.millennialmedia.intellibot.psi.ref.RobotPythonClass;
import com.millennialmedia.intellibot.psi.ref.RobotPythonFile;
import com.millennialmedia.intellibot.psi.util.PerformanceCollector;
import com.millennialmedia.intellibot.psi.util.ReservedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.io.File;

/**
 * @author Stephen Abrams
 */
public class HeadingImpl extends RobotPsiElementBase implements Heading {

    // ROBOT_BUILT_IN = "robot.libraries.BuiltIn" is more precise, but if PYTHONPATH include robot.libraries,
    // PythonResolver.findClass - safeFindClass will return BuiltIn.BuiltIn instead of robot.libraries.BuiltIn.BuiltIn,
    // So it will think class not found.
    private static final String ROBOT_BUILT_IN = "BuiltIn";
    private static final String WITH_NAME = "WITH NAME";
    private static final String DEFAULT_RESOURCE_NAME = "_ProjectDefaultResource_.robot";
    private static Collection<DefinedVariable> BUILT_IN_VARIABLES = null;
    private Collection<KeywordInvokable> invokedKeywords;
    private MultiMap<String, KeywordInvokable> invokableReferences;
    private Collection<Variable> usedVariables;
    private Collection<DefinedKeyword> definedKeywords;
    private Collection<DefinedKeyword> testCases;
    private Collection<KeywordFile> keywordFiles;
    private Collection<PsiFile> referencedFiles;
    private Collection<DefinedVariable> declaredVariables;

    public HeadingImpl(@NotNull final ASTNode node) {
        super(node);
        NotificationsConfiguration.getNotificationsConfiguration().register(
                "intellibot.debug", NotificationDisplayType.NONE);
    }

    private static void debug(@NotNull String lookup, String data, @NotNull Project project) {
        if (RobotOptionsProvider.getInstance(project).isDebug()) {
            String message = String.format("[HeadlingImpl][%s] %s", lookup, data);
            Notifications.Bus.notify(new Notification("intellibot.debug", "Debug", message, NotificationType.INFORMATION));
        }

    }

    @Override
    public boolean isSettings() {
        // TODO: better OO
        String text = getPresentableText();
        return text.startsWith("*** Setting");
    }

    private boolean containsVariables() {
        // TODO: better OO
        String text = getPresentableText();
        return text.startsWith("*** Variable");
    }

    @Override
    public boolean containsTestCases() {
        // TODO: better OO
        String text = getPresentableText();
        return text.startsWith("*** Test Case");
    }

    @Override
    public boolean containsKeywordDefinitions() {
        // TODO: better OO
        String text = getPresentableText();
        return text.startsWith("*** Keyword") || text.startsWith("*** User Keyword");
    }

    private boolean containsImports() {
        return isSettings();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        if (isSettings()) {
            PsiFile file = getContainingFile();
            if (file instanceof RobotFile) {
                ((RobotFile) file).importsChanged();
            }
        }
        this.definedKeywords = null;
        this.testCases = null;
        this.keywordFiles = null;
        this.invokedKeywords = null;
        this.invokableReferences = null;
        this.usedVariables = null;
        this.referencedFiles = null;
        this.declaredVariables = null;
        if (! getContainingFile().getName().equals(DEFAULT_RESOURCE_NAME))
            RobotProjectData.getInstance(getProject()).setProjectGlobalDefaultVariables(null);
    }

    @Override
    public void importsChanged() {
        this.definedKeywords = null;
        this.testCases = null;
        this.keywordFiles = null;
        this.invokedKeywords = null;
        this.invokableReferences = null;
        this.usedVariables = null;
        this.referencedFiles = null;
        this.declaredVariables = null;
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getDefinedVariables() {
        Collection<DefinedVariable> results = this.declaredVariables;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "defined variables");
            results = collectVariables();
            this.declaredVariables = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<DefinedVariable> collectVariables() {
        Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();
        if (! getContainingFile().getName().equals(DEFAULT_RESOURCE_NAME)) {
            addBuiltInVariables(results);
        }
        if (containsVariables()) {
            for (PsiElement child : getChildren()) {
                if (child instanceof DefinedVariable) {
                    results.add((DefinedVariable) child);
                }
            }
        } else if (containsImports()) {
            for (KeywordFile imported : getImportedFiles()) {
                if (imported.getImportType() == ImportType.VARIABLES) {
                    results.addAll(imported.getDefinedVariables());
                }
            }
        }
        if (! getContainingFile().getName().equals(DEFAULT_RESOURCE_NAME)) {
            addProjectDefaultVariables(results);
        }
        return results;
    }

    private void addBuiltInVariables(@NotNull Collection<DefinedVariable> variables) {
        variables.addAll(getBuiltInVariables());
    }

    // TODO: code highlight is not quite working; see KyleEtlPubAdPart.robot; think it has to do with name difference GLOBAL_VARIABLE vs CURDIR etc
    private synchronized Collection<DefinedVariable> getBuiltInVariables() {
        if (BUILT_IN_VARIABLES == null) {
            Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();

            for (ReservedVariable variable : ReservedVariable.values()) {
                PsiElement pythonVariable = variable.getVariable(getProject());
                if (pythonVariable != null) {
                    // already formatted ${X}
                    results.add(new VariableDto(pythonVariable, variable.getVariable(), variable.getScope()));
                }
            }

            BUILT_IN_VARIABLES = results.isEmpty() ?
                    Collections.<DefinedVariable>emptySet() :
                    Collections.unmodifiableCollection(results);
        }

        return BUILT_IN_VARIABLES;
    }

    // bug?
    // Project A, open Project B (this windows), then open Project A (this windows)
    // The first time of call sequence like below:
    // HeadlingImpl.collectVariables -> getImportedFiles -> collectImportFiles -> addBuiltInImports -> PythonResolver.findClass -> PythonResolver.safeFindClass -> PyClassNameIndex.find
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
                debug("getProjectDefaultVariables", filename + " : not found.", project);
            }

            variables = results.isEmpty() ?
                    Collections.<DefinedVariable>emptySet() :
                    Collections.unmodifiableCollection(results);
            robotProjectData.setProjectGlobalDefaultVariables(variables);
        }
        return variables;
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getDefinedKeywords() {
        Collection<DefinedKeyword> results = this.definedKeywords;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "defined keywords");
            results = collectDefinedKeywords();
            this.definedKeywords = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<DefinedKeyword> collectDefinedKeywords() {
        if (!containsKeywordDefinitions()) {
            return Collections.emptySet();
        }
        Collection<DefinedKeyword> results = new LinkedHashSet<DefinedKeyword>();
        for (PsiElement child : getChildren()) {
            if (child instanceof DefinedKeyword) {
                results.add(((DefinedKeyword) child));
            }
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getTestCases() {
        Collection<DefinedKeyword> results = this.testCases;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "defined test cases");
            results = collectTestCases();
            this.testCases = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<DefinedKeyword> collectTestCases() {
        if (!containsTestCases()) {
            return Collections.emptySet();
        }
        Collection<DefinedKeyword> results = new LinkedHashSet<DefinedKeyword>();
        for (PsiElement child : getChildren()) {
            if (child instanceof DefinedKeyword) {
                results.add(((DefinedKeyword) child));
            }
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables() {
        Collection<PsiFile> results = this.referencedFiles;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "files from invoked keywords");
            results = collectReferencedFiles();
            this.referencedFiles = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<PsiFile> collectReferencedFiles() {
        Collection<PsiFile> results = new HashSet<PsiFile>();
        for (KeywordInvokable keyword : getInvokedKeywords()) {
            PsiReference reference = keyword.getReference();
            if (reference != null) {
                PsiElement resolved = reference.resolve();
                if (resolved != null) {
                    results.add(resolved.getContainingFile());
                }
            }
            addReferencedArguments(results, keyword);
        }
        for (Variable variable : getUsedVariables()) {
            PsiReference reference = variable.getReference();
            if (reference != null) {
                PsiElement resolved = reference.resolve();
                if (resolved != null) {
                    results.add(resolved.getContainingFile());
                }
            }
        }
        return results;
    }

    private void addReferencedArguments(@NotNull Collection<PsiFile> results, @NotNull KeywordInvokable keyword) {
        for (Argument argument : keyword.getArguments()) {
            PsiReference reference = argument.getReference();
            if (reference != null) {
                PsiElement resolved = reference.resolve();
                if (resolved != null) {
                    results.add(resolved.getContainingFile());
                }
            }
        }
    }

    @Override
    @NotNull
    public Collection<KeywordInvokable> getInvokedKeywords() {
        Collection<KeywordInvokable> results = this.invokedKeywords;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "invoked keywords");
            results = collectInvokedKeywords();
            this.invokedKeywords = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<KeywordInvokable> collectInvokedKeywords() {
        return PsiTreeUtil.findChildrenOfType(this, KeywordInvokable.class);
    }

    @NotNull
    @Override
    public Collection<KeywordInvokable> getKeywordReferences(@Nullable KeywordDefinition definition) {
        MultiMap<String, KeywordInvokable> references = getKeywordReferences();
        return definition == null ? Collections.<KeywordInvokable>emptySet() : references.get(definition.getPresentableText());
    }

    @NotNull
    private MultiMap<String, KeywordInvokable> getKeywordReferences() {
        MultiMap<String, KeywordInvokable> results = this.invokableReferences;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "keyword references");
            results = collectKeywordReferences();
            this.invokableReferences = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private MultiMap<String, KeywordInvokable> collectKeywordReferences() {
        MultiMap<String, KeywordInvokable> results = new MultiMap<String, KeywordInvokable>();
        for (KeywordInvokable invokable : getInvokedKeywords()) {
            PsiReference reference = invokable.getReference();
            if (reference != null) {
                PsiElement element = reference.resolve();
                if (element instanceof KeywordDefinition) {
                    results.putValue(
                            ((KeywordDefinition) element).getPresentableText(),
                            invokable
                    );
                }
            }
        }
        return results;
    }

    @NotNull
    private Collection<Variable> getUsedVariables() {
        Collection<Variable> results = this.usedVariables;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "used variables");
            results = collectUsedVariables();
            this.usedVariables = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<Variable> collectUsedVariables() {
        return PsiTreeUtil.findChildrenOfType(this, Variable.class);
    }

    @NotNull
    @Override
    public Collection<KeywordFile> getImportedFiles() {
        Collection<KeywordFile> results = this.keywordFiles;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "imported files");
            results = collectImportFiles();
            this.keywordFiles = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<KeywordFile> collectImportFiles() {
        Collection<KeywordFile> files = new LinkedHashSet<KeywordFile>();
        if (! getContainingFile().getName().equals(DEFAULT_RESOURCE_NAME))
            addBuiltInImports(files);
        if (containsImports()) {
            Collection<Import> imports = PsiTreeUtil.findChildrenOfType(this, Import.class);
            for (Import imp : imports) {
                Argument argument = PsiTreeUtil.findChildOfType(imp, Argument.class);
                if (argument != null) {
                    if (imp.isResource()) {
                        PsiElement resolution = resolveImport(argument);
                        if (resolution instanceof KeywordFile) {
                            files.add((KeywordFile) resolution);
                        }
                    } else if (imp.isLibrary() || imp.isVariables()) {
                        PsiElement resolved = resolveImport(argument);
                        PyClass resolution = PythonResolver.castClass(resolved);
                        if (resolution != null) {
                            files.add(new RobotPythonClass(getNamespace(imp, argument), resolution,
                                    ImportType.getType(imp.getPresentableText())));
                        }
                        PyFile file = PythonResolver.castFile(resolved);
                        if (file != null) {
                            files.add(new RobotPythonFile(getNamespace(imp, argument), file,
                                    ImportType.getType(imp.getPresentableText())));
                        }
                    }
                }
            }
        }
        if (RobotOptionsProvider.getInstance(getProject()).searchChildKeywords()) {
            findChildrenClass(files, "keywords");
            // forcePatch(files);
        }
        return files;
    }


    /**
     * search sibling library by keywords
     * patch for SeleniumLibrary dynamic keywords
     * support all libraries that contain "keywords" package.
     *
     * @param files       all pyClass
     * @param libraryName default search is keywords.
     */
    void findChildrenClass(Collection<KeywordFile> files, String libraryName) {
        Collection<PyFile> fileList = PyModuleNameIndex.find(libraryName, getProject(), true);
        /* TODO: judge whether the PyFile is in subdirectory of a RobotPythonClass need import(in files)
           if no, don't import this library
           pyFile.getParent().getParent() ==
           RobotPythonClass.pythonClass.getContainingFile().getVirtualFile().getParent() ?
         */

        List<PsiFile> allFiles = new ArrayList<PsiFile>();
        for (PyFile pyFile : fileList) {
            /* tested, SeleniumLibrary,
            pyFile.getName() == "__init__.py"
            pyFile.getParent().getName() == "keywords"
            pyFile.getParent().getParent().getName() == "SeleniumLibrary"
            */
            if (pyFile.getParent() != null) {
                boolean toBeAdded = false;
                if (pyFile.getParent().getParent() != null) {
                    String name = pyFile.getParent().getParent().getName();
                    for (KeywordFile file: files) {
                        if (file instanceof RobotPythonClass) {
                            if (((RobotPythonClass) file).getDebugFileName().equals(name)) {
                                toBeAdded = true;
                                break;
                            }
                        }
                    }
                }
                if (toBeAdded) {
                    allFiles.addAll(Arrays.asList(pyFile.getParent().getFiles()));
                }
            }
        }
        for (PsiFile psiFile : allFiles) {
            // this is static library ,do not need to gen
            //if (psiFile.getNextSibling() instanceof RobotFileImpl) {
            if (psiFile instanceof RobotFileImpl) {
                continue;
            }
            PsiElement[] all = psiFile.getChildren();
            for (PsiElement psiElement : all) {
                if (psiElement instanceof PyClass) {
                    files.add(new RobotPythonClass(((PyClass) psiElement).getName(), (PyClass) psiElement, ImportType.LIBRARY));
                }
            }
        }
    }

//    public void forcePatch(Collection files) {
//        //Force Patch by Selenium
//        String[] sourcelist = {
//                "open_browser",
//                "get_cookies",
//                "input_text_into_prompt",
//                "get_webelement",
//                "submit_form",
//                "select_frame",
//                "execute_javascript",
//                "register_keyword_to_run_on_failure",
//                "set_screenshot_directory",
//                "get_list_items",
//                "get_table_cell",
//                "wait_for_condition",
//                "active_drivers",
//                "create_driver",
//                "select_window"
//        };
//        for (String str : sourcelist) {
//            Collection<PyFunction> funcs = PyFunctionNameIndex.find(str, getProject());
//            for (PyFunction pyfunc : funcs) {
//                PyClass cs = pyfunc.getContainingClass();
//                files.add(new RobotPythonClass(cs.getName(), cs, ImportType.LIBRARY));
//            }
//        }
//    }


    /**
     * Gets the namespace of the current import.  This looks for the 'WITH NAME' tag else returns the first argument.
     *
     * @param imp     the import statement to get the namespace of.
     * @param library the first argument; aka the default namespace
     * @return the namespace of the import.
     */
    private String getNamespace(Import imp, Argument library) {
        Argument[] args = PsiTreeUtil.getChildrenOfType(imp, Argument.class);
        int index = -1;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Argument arg = args[i];
                if (WITH_NAME.equals(arg.getPresentableText())) {
                    index = i;
                    break;
                }
            }
        }
        String results = library.getPresentableText();
        if (index > 0 && index + 1 < args.length) {
            results = args[index + 1].getPresentableText();
        }
        return results;
    }

    private void addBuiltInImports(@NotNull Collection<KeywordFile> files) {
        PyClass builtIn = PythonResolver.findClass(ROBOT_BUILT_IN, getProject());
        if (builtIn != null) {
            files.add(new RobotPythonClass(ROBOT_BUILT_IN, builtIn, ImportType.LIBRARY));
        }
    }

    @Nullable
    private PsiElement resolveImport(@NotNull Argument argument) {
        PsiReference reference = argument.getReference();
        if (reference != null) {
            return reference.resolve();
        }
        return null;
    }

    @Override
    @NotNull
    public Icon getIcon(int flags) {
        return RobotIcons.HEADING;
    }
}