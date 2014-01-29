package com.millennialmedia.intellibot.psi.ref;

import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
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
    private static final String UNDERSCORE_DEUX = "__";
    private static final String DOT = ".";
    private static final String EMPTY = "";

    private final String library;
    private final PyClass pythonClass;

    public RobotPythonClass(@NotNull String library, @NotNull PyClass pythonClass) {
        this.library = library;
        this.pythonClass = pythonClass;
    }

    @Nullable
    public PyFunction findMethodByKeyword(@NotNull String name) {
        String functionName = trimClassName(this.library, name);
        return pythonClass.findMethodByName(functionName, true);
    }

    @NotNull
    @Override
    public Collection<String> getKeywords() {
        final Collection<String> results = new HashSet<String>();
        this.pythonClass.visitMethods(new Processor<PyFunction>() {
            public boolean process(PyFunction function) {
                String keyword = functionToKeyword(function.getName());
                if (keyword != null) {
                    results.add(keyword);
                }
                return true;
            }
        }, true);
        return results;
    }

    private static String functionToKeyword(@Nullable String function) {
        if (function == null || function.contains(UNDERSCORE_DEUX)) {
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
