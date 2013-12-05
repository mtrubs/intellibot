package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class SettingImpl extends RobotPsiElementBase implements Setting {

    public SettingImpl(@NotNull final ASTNode node) {
        super(node);
    }
}
