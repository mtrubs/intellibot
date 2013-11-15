package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.KeywordInvokeable;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import org.jetbrains.annotations.NotNull;


/**
 * @author: Stephen Abrams
 */
public class KeywordInvokeableImpl extends RobotPsiElementBase implements KeywordInvokeable {

    public KeywordInvokeableImpl(@NotNull final ASTNode node) {
        super(node);
    }
}
