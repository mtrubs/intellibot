package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.Import;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class ImportImpl extends RobotPsiElementBase implements Import {

    public ImportImpl(@NotNull final ASTNode node) {
        super(node);
    }
}