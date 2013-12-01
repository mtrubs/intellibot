package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.BracketSetting;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class BracketSettingImpl extends RobotPsiElementBase implements BracketSetting {

    public BracketSettingImpl(@NotNull final ASTNode node) {
        super(node);
    }
}