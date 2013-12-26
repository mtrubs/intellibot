package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class VariableDefinitionImpl extends RobotPsiElementBase implements VariableDefinition {

    public VariableDefinitionImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.VARIABLE_DEFINITION);
    }
}
