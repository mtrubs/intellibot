package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author mrubino
 */
public interface KeywordStatement extends PsiElement {

    @Nullable
    KeywordInvokable getInvokable();

    @NotNull
    List<Argument> getArguments();
    
    @Nullable
    DefinedVariable getGlobalVariable();
}
