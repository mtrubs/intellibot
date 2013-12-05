package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
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