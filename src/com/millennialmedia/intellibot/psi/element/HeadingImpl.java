package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.MultiMap;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.millennialmedia.intellibot.ide.icons.RobotIcons;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * @author Stephen Abrams
 */
public class HeadingImpl extends RobotPsiElementBase implements Heading {

    private static final String ROBOT_BUILT_IN = "BuiltIn";
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
        addBuiltInVariables(results);
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
                    results.add(new VariableDto(pythonVariable, variable.getVariable()));
                }
            }

            BUILT_IN_VARIABLES = results.isEmpty() ?
                    Collections.<DefinedVariable>emptySet() :
                    Collections.unmodifiableCollection(results);
        }

        return BUILT_IN_VARIABLES;
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
                            files.add(new RobotPythonClass(argument.getPresentableText(), resolution,
                                    ImportType.getType(imp.getPresentableText())));
                        }
                        PyFile file = PythonResolver.castFile(resolved);
                        if (file != null) {
                            files.add(new RobotPythonFile(argument.getPresentableText(), file,
                                    ImportType.getType(imp.getPresentableText())));
                        }
                    }
                }
            }
        }
        return files;
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