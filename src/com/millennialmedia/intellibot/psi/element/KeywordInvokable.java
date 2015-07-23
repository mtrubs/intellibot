package com.millennialmedia.intellibot.psi.element;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Stephen Abrams
 */
public interface KeywordInvokable extends RobotStatement {

    @NotNull
    Collection<Argument> getArguments();
}
