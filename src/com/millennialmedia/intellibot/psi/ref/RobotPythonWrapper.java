package com.millennialmedia.intellibot.psi.ref;

import com.jetbrains.python.psi.PyParameter;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 * @since 2014-06-17
 */
public abstract class RobotPythonWrapper {

    private static final String UNDERSCORE = "_";
    private static final String SELF = "self";

    protected static boolean hasArguments(@Nullable PyParameter[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return false;
        }

        for (PyParameter parameter : parameters) {
            String name = parameter.getName();
            if (name != null && !SELF.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    protected static String functionToKeyword(@Nullable String function) {
        // these keeps out intended private functions
        if (function == null || function.startsWith(UNDERSCORE)) {
            return null;
        } else {
            return function;
        }
    }
}
