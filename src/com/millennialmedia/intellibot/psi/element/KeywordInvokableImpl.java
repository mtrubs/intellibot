package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.ref.RobotKeywordReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class KeywordInvokableImpl extends RobotPsiElementBase implements KeywordInvokable {

    public KeywordInvokableImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.KEYWORD);
    }

    @Override
    public String getPresentableText() {
        return getTextData();
    }

    @Override
    public PsiReference getReference() {
        return new RobotKeywordReference(this);
    }
}