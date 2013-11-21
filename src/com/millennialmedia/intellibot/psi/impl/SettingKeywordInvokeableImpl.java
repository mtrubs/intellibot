package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import com.millennialmedia.intellibot.psi.SettingKeywordInvokeable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class SettingKeywordInvokeableImpl extends RobotPsiElementBase implements SettingKeywordInvokeable {

    public SettingKeywordInvokeableImpl(@NotNull final ASTNode node) {
        super(node);
    }
}