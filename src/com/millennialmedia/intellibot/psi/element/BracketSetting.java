package com.millennialmedia.intellibot.psi.element;

/**
 * @author mrubino
 */
public interface BracketSetting extends RobotStatement {

    /**
     * Determines if the current element is and '[Arguments]' element.
     *
     * @return true if this is an argument element; false otherwise.
     */
    boolean isArguments();
}
