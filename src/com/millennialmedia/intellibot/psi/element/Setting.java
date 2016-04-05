package com.millennialmedia.intellibot.psi.element;

/**
 * @author mrubino
 */
public interface Setting extends RobotStatement {

    /**
     * Determines if the current element is a 'Suite Teardown' element.
     *
     * @return true if this is a suite teardown element; false otherwise.
     */
    boolean isSuiteTeardown();

    /**
     * Determines if the current element is a 'Test Teardown' element.
     *
     * @return true if this is a test teardown element; false otherwise.
     */
    boolean isTestTeardown();
}
