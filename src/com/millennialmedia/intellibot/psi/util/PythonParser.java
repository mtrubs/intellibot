package com.millennialmedia.intellibot.psi.util;

import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PythonParser {

    public static boolean keywordHasArguments(PyFunction keyword_function) {
        PyParameter[] parameters = keyword_function.getParameterList().getParameters();

        if (parameters.length == 0) {
            return false;
        }

        for (PyParameter parameter : parameters) {
            String name = parameter.getName();
            if (name != null && !"self".equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static String keywordName(PyFunction function) {
        String name = function.getName();
        String decoratorName = null;

        if (name == null || isPrivate(name)) {
            return null;
        }

        // Get info from @keyword
        PyDecorator keywordDecorator = getKeywordDecorator(function);
        if (keywordDecorator != null) {
            decoratorName = keywordNameFromDecorator(keywordDecorator);
        }

        return decoratorName != null ? decoratorName : name;
    }

    public static String keywordName(PyExpression expression) {
        String name = expression.getName();

        if (name == null || isPrivate(name)) {
            return null;
        }

        return name;
    }

    public static boolean isPrivate(@NotNull String keyword) {
        // these keeps out intended private functions
        return keyword.startsWith("_") || keyword.startsWith("ROBOT_LIBRARY_");
    }

    private static String keywordNameFromDecorator(PyDecorator decorator) {
        if (!decorator.hasArgumentList()) {
            return null;
        }

        PyExpression keywordNameArgument = decorator.getKeywordArgument("name");

        if (keywordNameArgument == null && decorator.getArguments()[0].getName() == null) {
            keywordNameArgument = decorator.getArguments()[0];
        }

        if (keywordNameArgument != null) {
            return keywordNameArgument.getText().replaceAll("^[\"']|[\"']$", "");
        }

        return null;
    }

    private static PyDecorator getKeywordDecorator(@NotNull PyFunction function) {
        PyDecoratorList decorators = function.getDecoratorList();

        if (decorators == null) {
            return null;
        }

        for (PyDecorator decorator : decorators.getDecorators()) {
            String decorator_name = decorator.getName();
            if (decorator_name != null && decorator_name.matches("^(robot.)?(api.)?(deco.)?keyword")) {
                return decorator;
            }
        }

        return null;
    }
}