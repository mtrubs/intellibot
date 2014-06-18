package com.millennialmedia.intellibot.psi.ref;

import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyTargetExpression;
import com.millennialmedia.intellibot.psi.dto.KeywordDto;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author mrubino
 */
public class RobotPythonClass implements KeywordFile {

    private static final String UNDERSCORE = "_";
    private static final String SELF = "self";

    private final String library;
    private final PyClass pythonClass;

    public RobotPythonClass(@NotNull String library, @NotNull PyClass pythonClass) {
        this.library = library;
        this.pythonClass = pythonClass;
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getDefinedKeywords() {
        final Collection<DefinedKeyword> results = new HashSet<DefinedKeyword>();
        final String namespace = this.library;
        this.pythonClass.visitMethods(new Processor<PyFunction>() {
            
            @Override
            public boolean process(PyFunction function) {
                String keyword = functionToKeyword(function.getName());
                if (keyword != null) {
                    results.add(new KeywordDto(function, namespace, keyword, hasArguments(function.getParameterList().getParameters())));
                }
                return true;
            }
        }, true);
        this.pythonClass.visitClassAttributes(new Processor<PyTargetExpression>() {

            @Override
            public boolean process(PyTargetExpression expression) {
                String keyword = functionToKeyword(expression.getName());
                if (keyword != null) {
                    results.add(new KeywordDto(expression, namespace, keyword, false));
                }
                return true;
            }
        }, true);
        return results;
    }

    private static boolean hasArguments(@Nullable PyParameter[] parameters) {
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

    private static String functionToKeyword(@Nullable String function) {
        // these keeps out intended private functions
        if (function == null || function.startsWith(UNDERSCORE)) {
            return null;
        } else {
            return function;
        }
    }
}
