package com.millennialmedia.intellibot.psi.ref;

import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyTargetExpression;
import com.millennialmedia.intellibot.psi.dto.KeywordDto;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author mrubino
 */
public class RobotPythonClass extends RobotPythonWrapper implements KeywordFile {

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
}
