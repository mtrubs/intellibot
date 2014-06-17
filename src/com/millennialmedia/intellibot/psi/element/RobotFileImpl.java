package com.millennialmedia.intellibot.psi.element;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import com.millennialmedia.intellibot.psi.RobotFeatureFileType;
import com.millennialmedia.intellibot.psi.RobotLanguage;
import com.millennialmedia.intellibot.psi.ref.PythonResolver;
import com.millennialmedia.intellibot.psi.ref.RobotPythonClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Stephen Abrams
 */
public class RobotFileImpl extends PsiFileBase implements RobotFile, KeywordFile {

    private static final String ROBOT_BUILT_IN = "BuiltIn";

    private Collection<KeywordDefinition> testDefinitions;
    private Collection<DefinedKeyword> definedKeywords;
    private Collection<KeywordFile> keywordFiles;

    public RobotFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, RobotLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return RobotFeatureFileType.getInstance();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.definedKeywords = null;
        this.testDefinitions = null;
        this.keywordFiles = null;
    }

    @NotNull
    @Override
    public Collection<KeywordInvokable> getInvokedKeywords() {
        Collection<KeywordInvokable> results = new HashSet<KeywordInvokable>();
        for (DefinedKeyword keyword : getKeywords()) {
            results.addAll(keyword.getInvokedKeywords());
        }
        for (KeywordDefinition testCase : getTestDefinitions()) {
            results.addAll(testCase.getInvokedKeywords());
        }
        return results;
    }
    
    private Collection<KeywordDefinition> getTestDefinitions() {
        Collection<KeywordDefinition> results = this.testDefinitions;
        if (results == null) {
            results = collectTestDefinitions();
            this.testDefinitions = results;
        }
        return results;
    }
    
    private Collection<KeywordDefinition> collectTestDefinitions() {
        List<KeywordDefinition> result = new ArrayList<KeywordDefinition>();
        for (PsiElement child : getChildren()) {
            if (child instanceof Heading && ((Heading) child).containsTestCases()) {
                for (PsiElement headingChild : child.getChildren()) {
                    if (headingChild instanceof KeywordDefinition)
                        result.add(((KeywordDefinition) headingChild));
                }
            }
        }
        return result;
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getKeywords() {
        Collection<DefinedKeyword> results = this.definedKeywords;
        if (results == null) {
            results = collectKeywords();
            this.definedKeywords = results;
        }
        return results;
    }

    @NotNull
    private Collection<DefinedKeyword> collectKeywords() {
        List<DefinedKeyword> result = new ArrayList<DefinedKeyword>();
        for (PsiElement child : getChildren()) {
            if (child instanceof Heading && ((Heading) child).containsKeywordDefinitions()) {
                for (PsiElement headingChild : child.getChildren()) {
                    if (headingChild instanceof DefinedKeyword)
                        result.add(((DefinedKeyword) headingChild));
                }
            }
        }
        return result;
    }

    @Override
    @NotNull
    public Collection<KeywordFile> getImportedFiles() {
        Collection<KeywordFile> results = this.keywordFiles;
        if (results == null) {
            results = collectImportFiles();
            this.keywordFiles = results;
        }
        return results;
    }

    private Collection<KeywordFile> collectImportFiles() {
        List<KeywordFile> files = new ArrayList<KeywordFile>();
        for (PsiElement child : getChildren()) {
            if (child instanceof Heading && ((Heading) child).containsImports()) {
                for (PsiElement headingChild : child.getChildren()) {
                    if (headingChild instanceof Import) {
                        Import imp = (Import) headingChild;
                        if (imp.isResource()) {
                            Argument argument = PsiTreeUtil.findChildOfType(imp, Argument.class);
                            if (argument != null) {
                                PsiElement resolution = resolveImport(argument);
                                if (resolution instanceof KeywordFile) {
                                    files.add((KeywordFile) resolution);
                                }
                            }
                        } else if (imp.isLibrary()) {
                            Argument argument = PsiTreeUtil.findChildOfType(imp, Argument.class);
                            if (argument != null) {
                                PyClass resolution = PythonResolver.cast(resolveImport(argument));
                                if (resolution != null) {
                                    files.add(new RobotPythonClass(argument.getPresentableText(), resolution));
                                }
                            }
                        }
                    }
                }
            }
        }
        addBuiltIn(files);
        return files;
    }

    private void addBuiltIn(List<KeywordFile> files) {
        PyClass builtIn = PythonResolver.findClass(ROBOT_BUILT_IN, getProject());
        if (builtIn != null) {
            files.add(new RobotPythonClass(ROBOT_BUILT_IN, builtIn));
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
}
