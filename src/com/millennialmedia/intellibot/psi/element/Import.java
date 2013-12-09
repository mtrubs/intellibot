package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;

/**
 * @author mrubino
 */
public interface Import extends PsiElement {

    boolean isResource();
}
