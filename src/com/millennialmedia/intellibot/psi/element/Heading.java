package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Stephen Abrams
 */
public interface Heading extends RobotStatement {

    boolean isSettings();

    boolean containsTestCases();

    boolean containsKeywordDefinitions();

    @NotNull
    Collection<KeywordFile> getImportedFiles();

    @NotNull
    Collection<DefinedKeyword> getDefinedKeywords();

    @NotNull
    Collection<DefinedKeyword> getTestCases();

    @NotNull
    Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables();

    @NotNull
    Collection<DefinedVariable> getDefinedVariables();

    void importsChanged();

    @NotNull
    Collection<KeywordInvokable> getInvokedKeywords();

    @NotNull
    Collection<KeywordInvokable> getKeywordReferences(@Nullable KeywordDefinition definition);
}
