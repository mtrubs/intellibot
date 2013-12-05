package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;

/**
 * @author Stephen Abrams
 */
public interface KeywordDefinition extends PsiElement {

    String getPresentableText();

    boolean matches(String text);
}
