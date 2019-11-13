package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;

/**
 * @author mrubino
 * @since 2014-06-06
 */
public interface DefinedKeyword {

    String getKeywordName();

    String getNamespace();

    /**
     * @return true if this keyword definition has the [Arguments] setting.
     */
    boolean hasArguments();

    boolean matches(String text);

    PsiElement reference();
}
