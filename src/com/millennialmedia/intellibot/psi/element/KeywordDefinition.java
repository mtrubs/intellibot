package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * @author Stephen Abrams
 */
public interface KeywordDefinition extends RobotStatement, PsiNamedElement {

    @NotNull
    List<KeywordInvokable> getInvokedKeywords();

    /**
     * This does not include variables that are saved globally as a result of calling this keyword.
     *
     * @return a list of variables as defined in the arguments of this keyword
     */
    @NotNull
    Collection<DefinedVariable> getDeclaredVariables();
}
