package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Stephen Abrams
 */
public interface KeywordInvokable extends PsiElement {

    String getPresentableText();

    @NotNull
    Collection<Argument> getArguments();
}
