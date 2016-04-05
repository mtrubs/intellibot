package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2016-04-01
 */
public class VariableDefinitionIdImpl extends RobotPsiElementBase implements VariableDefinitionId {

    public VariableDefinitionIdImpl(@NotNull ASTNode node) {
        super(node);
    }
}
