package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author mrubino
 * @since 2014-06-06
 */
public interface DefinedKeyword {
    
    String getKeywordName();
    
    boolean hasArguments();

    boolean matches(String text);
    
    PsiElement reference();
    
    @NotNull
    Collection<KeywordInvokable> getInvokedKeywords();
}
