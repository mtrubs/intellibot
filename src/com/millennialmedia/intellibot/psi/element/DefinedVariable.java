package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 * @since 2014-06-18
 */
public interface DefinedVariable {

    boolean matches(String text);

    @Nullable
    PsiElement reference();
}
