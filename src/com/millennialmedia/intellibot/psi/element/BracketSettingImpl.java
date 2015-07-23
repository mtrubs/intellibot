package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class BracketSettingImpl extends RobotPsiElementBase implements BracketSetting {

    private static final String ARGUMENTS = "[Arguments]";

    public BracketSettingImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    public boolean isArguments() {
        // TODO: better OO
        return ARGUMENTS.equalsIgnoreCase(getPresentableText());
    }
}