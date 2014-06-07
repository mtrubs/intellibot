package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.psi.PsiElement;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public interface SimpleInspection {

    /**
     * Determines if the element is valid for this inspection.
     *
     * @param element the element in question.
     * @return true if this element is valid; false if it is not.
     */
    boolean skip(PsiElement element);

    /**
     * The inspection message to be show in this inspection matches.
     *
     * @return the inspection message.
     */
    String getMessage();
}
