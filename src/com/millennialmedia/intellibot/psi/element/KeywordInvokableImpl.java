package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.psi.ref.RobotKeywordReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Stephen Abrams
 */
public class KeywordInvokableImpl extends RobotPsiElementBase implements KeywordInvokable {

    public KeywordInvokableImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Collection<Argument> getArguments() {
        PsiElement parent = getParent();
        if (parent instanceof KeywordStatement) {
            return ((KeywordStatement) parent).getArguments();
        }
        return Collections.emptySet();
    }

    @Override
    public PsiReference getReference() {
        return new RobotKeywordReference(this);
    }
}