package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Stephen Abrams
 */
public interface KeywordDefinition extends PsiElement {

    String getPresentableText();

    @NotNull
    Collection<KeywordInvokable> getInvokedKeywords();
}
