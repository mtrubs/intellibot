package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.Heading;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class HeadingImpl extends RobotPsiElementBase implements Heading {

    public HeadingImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    protected String getPresentableText() {
        ASTNode node = getNode();
        ASTNode firstText = node.findChildByType(RobotTokenTypes.HEADING);
        if (firstText != null) {
            return firstText.getText();
        }
        return super.getPresentableText();
    }
}