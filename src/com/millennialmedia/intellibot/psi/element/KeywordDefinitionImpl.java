package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class KeywordDefinitionImpl extends RobotPsiElementBase implements KeywordDefinition, DefinedKeyword {

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

    @Override
    public String getKeywordName() {
        return getPresentableText();
    }

    @Override
    public boolean hasArguments() {
        for (PsiElement child : getChildren()) {
            if (child instanceof BracketSetting) {
                BracketSetting bracket = (BracketSetting) child;
                if (bracket.isArguments()) {
                    return bracket.hasArgs();
                }
            }
        }
        return false;
    }
}
