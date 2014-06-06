package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiElement;

/**
 * @author mrubino
 */
public interface BracketSetting extends PsiElement {

    /**
     * Determines if the current element is and '[Arguments]' element.
     *
     * @return true if this is an argument element; false otherwise.
     */
    boolean isArguments();

    /**
     * Determines if this element has arguments under it.  Note this is not related
     * to the Arguments element.
     *
     * @return true if this element has arguments; false otherwise.
     */
    boolean hasArgs();
}
