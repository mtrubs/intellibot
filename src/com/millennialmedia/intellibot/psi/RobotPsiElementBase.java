package com.millennialmedia.intellibot.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Stephen Abrams
 */
public abstract class RobotPsiElementBase extends ASTWrapperPsiElement {
    public RobotPsiElementBase(@NotNull final ASTNode node) {
        super(node);
    }
}
