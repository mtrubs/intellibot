package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Stephen Abrams
 */
public interface KeywordInvokable extends RobotStatement, PsiNamedElement {

    @NotNull
    Collection<Argument> getArguments();
}
