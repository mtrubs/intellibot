package com.millennialmedia.intellibot.psi.element;

/**
 * @author mrubino
 */
public interface Import extends RobotStatement {

    boolean isResource();

    boolean isLibrary();
    
    boolean isVariables();
}
