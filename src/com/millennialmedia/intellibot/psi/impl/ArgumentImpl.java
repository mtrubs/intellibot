package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.Argument;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class ArgumentImpl extends RobotPsiElementBase implements Argument {
    public ArgumentImpl(@NotNull final ASTNode node) {
        super(node);
    }
}
