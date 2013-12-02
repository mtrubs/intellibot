package com.millennialmedia.intellibot.psi;

import com.intellij.psi.PsiElement;

/**
 * @author Stephen Abrams
 */
public interface Heading extends PsiElement {

    boolean isSettings();

    boolean containsKeywordDefinitions();
}
