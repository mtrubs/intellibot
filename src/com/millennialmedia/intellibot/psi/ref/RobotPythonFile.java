package com.millennialmedia.intellibot.psi.ref;

import com.jetbrains.python.psi.PyFile;
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
 * @since 2014-06-17
 */
public class RobotPythonFile extends RobotPythonWrapper implements KeywordFile {

    private final String library;
    private final PyFile pythonFile;

    public RobotPythonFile(@NotNull String library, @NotNull PyFile pythonFile) {
        this.library = library;
        this.pythonFile = pythonFile;
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getDefinedKeywords() {
        Collection<DefinedKeyword> results = new HashSet<DefinedKeyword>();
        for (PyFunction function : this.pythonFile.getTopLevelFunctions()) {
            String keyword = functionToKeyword(function.getName());
            if (keyword != null) {
                results.add(new KeywordDto(function, this.library, keyword, hasArguments(function.getParameterList().getParameters())));
            }
        }
        for (PyTargetExpression expression : this.pythonFile.getTopLevelAttributes()) {
            String keyword = functionToKeyword(expression.getName());
            if (keyword != null) {
                results.add(new KeywordDto(expression, this.library, keyword, false));
            }
        }
        return results;
    }
}
