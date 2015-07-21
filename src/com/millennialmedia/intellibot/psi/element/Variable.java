package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;

/**
 * @author mrubino
 * @since 2015-07-21
 */
public interface Variable extends PsiElement {

    String getPresentableText();
}
