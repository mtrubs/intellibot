package com.millennialmedia.intellibot.psi.element;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.millennialmedia.intellibot.psi.RobotElementType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Stephen Abrams
 */
public abstract class RobotPsiElementBase extends ASTWrapperPsiElement {

    private static final String EMPTY = "";

    private final RobotElementType elementType;

    public RobotPsiElementBase(@NotNull final ASTNode node, RobotElementType elementType) {
        super(node);
        this.elementType = elementType;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                return RobotPsiElementBase.this.getInternalText();
            }

            public String getLocationString() {
                return null;
            }

            public Icon getIcon(final boolean open) {
                return RobotPsiElementBase.this.getIcon(Iconable.ICON_FLAG_VISIBILITY);
            }
        };
    }

    protected String getTextData() {
        ItemPresentation presentation = getPresentation();
        if (presentation != null) {
            return presentation.getPresentableText();
        }
        return null;
    }

    private String getInternalText() {
        ASTNode node = getNode();
        ASTNode firstText = node.findChildByType(this.elementType);
        if (firstText != null) {
            return firstText.getText();
        }
        return EMPTY;
    }
}
