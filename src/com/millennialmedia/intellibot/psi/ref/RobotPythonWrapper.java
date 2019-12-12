package com.millennialmedia.intellibot.psi.ref;

import com.intellij.util.Processor;
import com.jetbrains.python.psi.*;
import com.millennialmedia.intellibot.psi.dto.KeywordDto;
import com.millennialmedia.intellibot.psi.dto.VariableDto;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.DefinedVariable;
import com.millennialmedia.intellibot.psi.util.PythonParser;
import com.millennialmedia.intellibot.psi.util.ReservedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author mrubino
 * @since 2014-06-17
 */
public abstract class RobotPythonWrapper {

    protected static void addDefinedVariables(@NotNull PyClass pythonClass, @NotNull final Collection<DefinedVariable> results) {
        pythonClass.visitClassAttributes(
                new Processor<PyTargetExpression>() {
                    @Override
                    public boolean process(PyTargetExpression expression) {
                        String keyword = PythonParser.keywordName(expression);
                        if (keyword != null) {
                            // not formatted ${X}, assume scalar
                            results.add(new VariableDto(expression, ReservedVariable.wrapToScalar(keyword), null));
                        }
                        return true;
                    }
                },
                true,
                null
        );
    }

    protected static void addDefinedKeywords(@NotNull PyClass pythonClass, @NotNull final String namespace, @NotNull final Collection<DefinedKeyword> results) {
        pythonClass.visitMethods(
                new Processor<PyFunction>() {

                    @Override
                    public boolean process(PyFunction function) {
                        String keyword = PythonParser.keywordName(function);
                        if (keyword != null) {
                            results.add(new KeywordDto(function, namespace, keyword, PythonParser.keywordHasArguments(function)));
                        }
                        return true;
                    }
                },
                true,
                null
        );
        pythonClass.visitClassAttributes(
                new Processor<PyTargetExpression>() {

                    @Override
                    public boolean process(PyTargetExpression expression) {
                        String keyword = PythonParser.keywordName(expression);
                        if (keyword != null) {
                            results.add(new KeywordDto(expression, namespace, keyword, false));
                        }
                        return true;
                    }
                },
                true,
                null
        );
    }
}
