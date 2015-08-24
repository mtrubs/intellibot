package com.millennialmedia.intellibot.psi.element;

/**
 * @author mrubino
 */
public interface VariableDefinition extends RobotStatement {

    // TODO: this should go away once we identify the nesting correctly
    boolean isNested();
}
