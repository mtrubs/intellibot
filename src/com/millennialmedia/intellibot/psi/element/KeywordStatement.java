package com.millennialmedia.intellibot.psi.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author mrubino
 */
public interface KeywordStatement extends RobotStatement {

    @Nullable
    KeywordInvokable getInvokable();

    @NotNull
    List<Argument> getArguments();
    
    @Nullable
    DefinedVariable getGlobalVariable();
}
