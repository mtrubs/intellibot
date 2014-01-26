package com.millennialmedia.intellibot.psi.ref;

import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 */
public class RobotPythonClass {

    private final String library;
    private final PyClass pythonClass;

    public RobotPythonClass(@NotNull String library, @NotNull PyClass pythonClass) {
        this.library = library;
        this.pythonClass = pythonClass;
    }

    public String getLibrary() {
        return this.library;
    }

    @Nullable
    public PyFunction findMethodByName(String name) {
        return pythonClass.findMethodByName(name, true);
    }
}
