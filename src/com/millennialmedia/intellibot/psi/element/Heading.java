package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Stephen Abrams
 */
public interface Heading extends PsiElement {

    boolean isSettings();

    boolean containsTestCases();

    boolean containsKeywordDefinitions();

    @NotNull
    Collection<KeywordFile> getImportedFiles();

    @NotNull
    Collection<DefinedKeyword> getDefinedKeywords();

    @NotNull
    Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables();

    @NotNull
    Collection<DefinedVariable> getDefinedVariables();

    void importsChanged();
}
