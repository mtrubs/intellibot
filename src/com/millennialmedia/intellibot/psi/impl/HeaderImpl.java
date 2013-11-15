package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.KeywordInvokeable;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Stephen Abrams
 */
public class HeaderImpl extends RobotPsiElementBase implements KeywordInvokeable {

    public HeaderImpl(@NotNull final ASTNode node) {
        super(node);
    }
}