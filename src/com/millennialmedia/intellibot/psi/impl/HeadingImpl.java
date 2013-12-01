package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
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
    public boolean isSettings() {
        String text = getTextData();
        return text != null && text.startsWith("*** Setting");
    }

    @Override
    public boolean containsKeywordDefinitions() {
        String text = getTextData();
        return text != null && (text.startsWith("*** Keyword") || text.startsWith("*** User Keyword"));
    }


    private String getTextData() {
        ItemPresentation presentation = getPresentation();
        if (presentation != null) {
            return presentation.getPresentableText();
        }
        return null;
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