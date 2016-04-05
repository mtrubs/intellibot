package com.millennialmedia.intellibot.psi.element;

/**
 * @author mrubino
 */
public interface BracketSetting extends RobotStatement {

    /**
     * Determines if the current element is an '[Arguments]' element.
     *
     * @return true if this is an argument element; false otherwise.
     */
    boolean isArguments();

    /**
     * Determines if the current element is a '[Teardown]' element.
     *
     * @return true if this is a teardown element; false otherwise.
     */
    boolean isTeardown();
}
