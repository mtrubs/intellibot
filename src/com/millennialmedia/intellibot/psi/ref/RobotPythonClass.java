package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
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
import java.util.regex.Pattern;

/**
 * @author mrubino
 */
public class RobotPythonClass implements KeywordFile {

    private static final String SPACE = " ";
    private static final String UNDERSCORE = "_";
    private static final String DOT = ".";
    private static final String EMPTY = "";
    private static final String SELF = "self";

    private final String library;
    private final PyClass pythonClass;

    public RobotPythonClass(@NotNull String library, @NotNull PyClass pythonClass) {
        this.library = library;
        this.pythonClass = pythonClass;
    }

    @Nullable
    public PsiElement findMethodByKeyword(@NotNull String name) {
        String functionName = trimClassName(this.library, name);
        // we do visit methods instead of find by name because we not want case to come into play
        InsensitiveNameFinder<PyFunction> byFuction = new InsensitiveNameFinder<PyFunction>(functionName);
        this.pythonClass.visitMethods(byFuction, true);
        PyFunction function = byFuction.getResult();
        if (function != null) {
            return function;
        }

        InsensitiveNameFinder<PyTargetExpression> byExpression = new InsensitiveNameFinder<PyTargetExpression>(functionName);
        this.pythonClass.visitClassAttributes(byExpression, true);
        return byExpression.getResult();
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getKeywords() {
        final Collection<DefinedKeyword> results = new HashSet<DefinedKeyword>();
        this.pythonClass.visitMethods(new Processor<PyFunction>() {
            public boolean process(PyFunction function) {
                String keyword = functionToKeyword(function.getName());
                if (keyword != null) {
                    results.add(new KeywordDto(keyword, hasArguments(function.getParameterList().getParameters())));
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
            return function.replaceAll(UNDERSCORE, SPACE);
        }
    }

    @NotNull
    private static String trimClassName(@Nullable String className, @NotNull String keyword) {
        // TODO: python functions can also be qualified with their class name to avoid ambiguity
        // TODO: is this enough?
        if (className != null && keyword.startsWith(className)) {
            keyword = keyword.replaceFirst(Pattern.quote(className + DOT), EMPTY);
        }
        // TODO: python keywords can have underscores or not; we should encourage not... i think
        return keyword.toLowerCase().replace(SPACE, UNDERSCORE);
    }
}
