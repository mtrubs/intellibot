package com.millennialmedia.intellibot.psi.ref;

import com.intellij.openapi.project.Project;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyTargetExpression;
import com.millennialmedia.intellibot.psi.dto.ImportType;
import com.millennialmedia.intellibot.psi.dto.KeywordDto;
import com.millennialmedia.intellibot.psi.dto.VariableDto;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.DefinedVariable;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import com.millennialmedia.intellibot.psi.util.PerformanceCollector;
import com.millennialmedia.intellibot.psi.util.PerformanceEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author mrubino
 * @since 2014-06-17
 */
public class RobotPythonFile extends RobotPythonWrapper implements KeywordFile, PerformanceEntity {

    private static final String EMPTY = "";

    private final String library;
    private final PyFile pythonFile;
    private final ImportType importType;

    public RobotPythonFile(@NotNull String library, @NotNull PyFile pythonFile, @NotNull ImportType importType) {
        this.library = library;
        this.pythonFile = pythonFile;
        this.importType = importType;
    }

    @NotNull
    @Override
    public Collection<DefinedKeyword> getDefinedKeywords() {
        PerformanceCollector debug = new PerformanceCollector(this, "get defined keywords");
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
        for (PyClass subClass : this.pythonFile.getTopLevelClasses()) {
            String namespace = subClass.getQualifiedName() == null ? EMPTY : subClass.getQualifiedName();
            addDefinedKeywords(subClass, namespace, results);
        }
        debug.complete();
        return results;
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getDefinedVariables() {
        PerformanceCollector debug = new PerformanceCollector(this, "get defined variables");
        final Collection<DefinedVariable> results = new HashSet<DefinedVariable>();
        for (PyTargetExpression expression : this.pythonFile.getTopLevelAttributes()) {
            String keyword = expression.getName();
            if (keyword != null) {
                results.add(new VariableDto(expression, keyword));
            }
        }
        for (PyClass subClass : this.pythonFile.getTopLevelClasses()) {
            addDefinedVariables(subClass, results);
        }
        debug.complete();
        return results;
    }

    @NotNull
    @Override
    public ImportType getImportType() {
        return this.importType;
    }

    @NotNull
    @Override
    public Collection<KeywordFile> getImportedFiles(boolean includeTransitive) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RobotPythonFile that = (RobotPythonFile) o;
        return this.pythonFile.equals(that.pythonFile);
    }

    @Override
    public int hashCode() {
        return this.pythonFile.hashCode();
    }

    @Override
    public String toString() {
        return this.library;
    }

    @NotNull
    @Override
    public String getDebugFileName() {
        return toString();
    }

    @NotNull
    @Override
    public String getDebugText() {
        return this.pythonFile.getContainingFile().getVirtualFile().getName();
    }

    @NotNull
    @Override
    public Project getProject() {
        return this.pythonFile.getProject();
    }
}
