package com.millennialmedia.intellibot.psi.ref;

import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyTargetExpression;
import com.millennialmedia.intellibot.psi.dto.KeywordDto;
import com.millennialmedia.intellibot.psi.dto.VariableDto;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.DefinedVariable;
import com.millennialmedia.intellibot.psi.util.ReservedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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
        if (function == null || isPrivate(function)) {
            return null;
        } else {
            return function;
        }
    }

    protected static void addDefinedVariables(@NotNull PyClass pythonClass, @NotNull final Collection<DefinedVariable> results) {
        pythonClass.visitClassAttributes(
                new Processor<PyTargetExpression>() {
                    @Override
                    public boolean process(PyTargetExpression expression) {
                        String keyword = expression.getName();
                        if (keyword != null && !isPrivate(keyword)) {
                            // not formatted ${X}, assume scalar
                            results.add(new VariableDto(expression, ReservedVariable.wrapToScalar(keyword), null));
                        }
                        return true;
                    }
                },
                true
        );
    }

    private static boolean isPrivate(@NotNull String keyword) {
        // these keeps out intended private functions
        return keyword.startsWith(UNDERSCORE) || keyword.startsWith("ROBOT_LIBRARY_");
    }

    protected static void addDefinedKeywords(@NotNull PyClass pythonClass, @NotNull final String namespace, @NotNull final Collection<DefinedKeyword> results) {
        pythonClass.visitMethods(
                new Processor<PyFunction>() {

                    @Override
                    public boolean process(PyFunction function) {
                        String keyword = functionToKeyword(function.getName());
                        if (keyword != null) {
                            results.add(new KeywordDto(function, namespace, keyword, hasArguments(function.getParameterList().getParameters())));
                        }
                        return true;
                    }
                },
                true
        );
        pythonClass.visitClassAttributes(
                new Processor<PyTargetExpression>() {

                    @Override
                    public boolean process(PyTargetExpression expression) {
                        String keyword = functionToKeyword(expression.getName());
                        if (keyword != null) {
                            results.add(new KeywordDto(expression, namespace, keyword, false));
                        }
                        return true;
                    }
                },
                true
        );
    }
}
