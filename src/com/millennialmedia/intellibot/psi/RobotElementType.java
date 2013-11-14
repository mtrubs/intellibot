package com.millennialmedia.intellibot.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class RobotElementType extends IElementType {
    public RobotElementType(@NotNull @NonNls String debugName) {
        super(debugName, com.millennialmedia.intellibot.psi.RobotLanguage.INSTANCE);
    }
}
