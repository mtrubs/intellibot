package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author mrubino
 */
public interface KeywordStatement extends PsiElement {

    @NotNull
    Collection<Argument> getArguments();
}
