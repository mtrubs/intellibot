package com.millennialmedia.intellibot.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Stephen Abrams
 */
public abstract class RobotPsiElementBase extends ASTWrapperPsiElement {

    private static final String ELLIPSIS = "...";

    public RobotPsiElementBase(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                return RobotPsiElementBase.this.getPresentableText();
            }

            public String getLocationString() {
                return null;
            }

            public Icon getIcon(final boolean open) {
                return RobotPsiElementBase.this.getIcon(Iconable.ICON_FLAG_VISIBILITY);
            }
        };
    }

    protected String getPresentableText() {
        return ELLIPSIS;
    }
}
