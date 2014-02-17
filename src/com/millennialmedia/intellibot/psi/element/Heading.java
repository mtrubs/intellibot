package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;

/**
 * @author Stephen Abrams
 */
public interface Heading extends PsiElement {

    boolean isSettings();

    boolean containsTestCases();

    boolean containsKeywordDefinitions();

    boolean containsImports();
}
