package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.ref.RobotImportReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class ImportImpl extends RobotPsiElementBase implements Import {

    public ImportImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.ARGUMENT);
    }

    @Override
    public PsiReference getReference() {
        return new RobotImportReference(this);
    }

    @Override
    public String getPresentableText() {
        return getTextData();
    }
}