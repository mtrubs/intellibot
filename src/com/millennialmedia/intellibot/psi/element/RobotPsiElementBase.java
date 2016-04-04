package com.millennialmedia.intellibot.psi.element;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.millennialmedia.intellibot.psi.util.PatternUtil;
import com.millennialmedia.intellibot.psi.util.PerformanceEntity;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Stephen Abrams
 */
public abstract class RobotPsiElementBase extends ASTWrapperPsiElement implements PerformanceEntity, RobotStatement {

    public RobotPsiElementBase(@NotNull final ASTNode node) {
        super(node);
    }

    @NotNull
    private static String toPresentableText(ASTNode node) {
        return PatternUtil.getPresentableText(node.getText());
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

    @NotNull
    @Override
    public String getPresentableText() {
        return toPresentableText(getNode());
    }

    @NotNull
    @Override
    public String getDebugFileName() {
        return getContainingFile().getVirtualFile().getName();
    }

    @NotNull
    @Override
    public String getDebugText() {
        return getPresentableText();
    }

    @NotNull
    public String getName() {
        return getPresentableText();
    }

    public PsiElement setName(@NotNull String var1) throws IncorrectOperationException {
        // TODO: for renaming an element
        return this;
    }
}
