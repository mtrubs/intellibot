package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.psi.Import;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class ImportImpl extends RobotPsiElementBase implements Import {

    public ImportImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    public String getPresentableText() {
        ASTNode node = getNode();
        ASTNode firstText = node.findChildByType(RobotTokenTypes.ARGUMENT);
        if (firstText != null) {
            return firstText.getText();
        }
        return super.getPresentableText();
    }

    @Override
    public PsiReference getReference() {
        return new RobotImportReference(this);
    }
}