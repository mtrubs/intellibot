/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi.ref;

import com.intellij.openapi.project.Project;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author mrubino
 */
public class PythonResolver {

    private PythonResolver() {
    }

    @Nullable
    public static PyClass findClass(String name, Project project) {
        Collection<PyClass> classes = PyClassNameIndex.find(name, project, true);
        if (classes == null || classes.isEmpty()) {
            return PyClassNameIndex.findClass(name, project);
        }
        // TODO: what if there is more than one class found?
        return classes.iterator().next();
    }
}
