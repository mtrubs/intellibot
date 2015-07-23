package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.psi.ref.RobotVariableReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2015-07-15
 */
public class VariableImpl extends RobotPsiElementBase implements Variable {

    public VariableImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RobotVariableReference(this);
    }
}
