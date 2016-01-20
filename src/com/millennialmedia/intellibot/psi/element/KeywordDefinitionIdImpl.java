package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2016-01-06
 */
public class KeywordDefinitionIdImpl extends RobotPsiElementBase implements KeywordDefinitionId {

    public KeywordDefinitionIdImpl(@NotNull ASTNode node) {
        super(node);
    }
}
