package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.KeywordDefinition;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class KeywordDefinitionImpl extends RobotPsiElementBase implements KeywordDefinition {

    public KeywordDefinitionImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    public String getPresentableText() {
        ASTNode node = getNode();
        ASTNode firstText = node.findChildByType(RobotTokenTypes.KEYWORD_DEFINITION);
        if (firstText != null) {
            return firstText.getText();
        }
        return super.getPresentableText();
    }
}
