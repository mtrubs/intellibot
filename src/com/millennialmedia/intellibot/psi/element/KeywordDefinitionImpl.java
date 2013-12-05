package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class KeywordDefinitionImpl extends RobotPsiElementBase implements KeywordDefinition {

    public KeywordDefinitionImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.KEYWORD_DEFINITION);
    }

    @Override
    public String getPresentableText() {
        return getTextData();
    }

    @Override
    public boolean matches(String text) {
        String myText = getPresentableText();
        if (myText == null) {
            return text == null;
        } else {
            // TODO: add inline argument matching/detection
            return myText.equals(text);
        }
    }
}
