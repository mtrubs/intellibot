package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
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
public class HeadingImpl extends RobotPsiElementBase implements Heading {

    private static final String ROBOT_BUILT_IN = "BuiltIn";

    private Collection<KeywordInvokable> invokedKeywords;
    private Collection<DefinedKeyword> definedKeywords;
    private Collection<KeywordFile> keywordFiles;
    private Collection<PsiFile> referencedFiles;

    public HeadingImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.HEADING);
    }

    @Override
    public boolean isSettings() {
        // TODO: better OO
        String text = getTextData();
        return text != null && text.startsWith("*** Setting");
    }

    @Override
    public boolean containsTestCases() {
        // TODO: better OO
        String text = getTextData();
        return text != null && text.startsWith("*** Test Case");
    }

    @Override
    public boolean containsKeywordDefinitions() {
        // TODO: better OO
        String text = getTextData();
        return text != null && (text.startsWith("*** Keyword") || text.startsWith("*** User Keyword"));
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.definedKeywords = null;
        this.keywordFiles = null;
        this.invokedKeywords = null;
        this.referencedFiles = null;
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getDefinedKeywords() {
        Collection<DefinedKeyword> results = this.definedKeywords;
        if (results == null) {
            results = collectDefinedKeywords();
            this.definedKeywords = results;
        }
        return results;
    }

    @NotNull
    private Collection<DefinedKeyword> collectDefinedKeywords() {
        List<DefinedKeyword> result = new ArrayList<DefinedKeyword>();
        for (PsiElement child : getChildren()) {
            if (child instanceof DefinedKeyword) {
                result.add(((DefinedKeyword) child));
            }
        }
        return result;
    }

    @NotNull
    @Override
    public Collection<PsiFile> getFilesFromInvokedKeywords() {
        Collection<PsiFile> results = this.referencedFiles;
        if (results == null) {
            results = collectReferencedFiles();
            this.referencedFiles = results;
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

    @NotNull
    private Collection<KeywordInvokable> getInvokedKeywords() {
        Collection<KeywordInvokable> results = this.invokedKeywords;
        if (results == null) {
            results = collectInvokedKeywords();
            this.invokedKeywords = results;
        }
        return results;
    }

    @NotNull
    private Collection<KeywordInvokable> collectInvokedKeywords() {
        List<KeywordInvokable> results = new ArrayList<KeywordInvokable>();
        for (PsiElement child : getChildren()) {
            if (child instanceof KeywordStatement) {
                for (PsiElement statement : child.getChildren()) {
                    if (statement instanceof KeywordInvokable) {
                        results.add((KeywordInvokable) statement);
                    }
                }
            }
        }
        for (DefinedKeyword definedKeyword : getDefinedKeywords()) {
            results.addAll(definedKeyword.getInvokedKeywords());
        }
        return results;
    }

    @NotNull
    @Override
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
            if (child instanceof Import) {
                Import imp = (Import) child;
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