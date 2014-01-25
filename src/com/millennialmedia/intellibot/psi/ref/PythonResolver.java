/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi.ref;

import com.intellij.openapi.project.Project;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author mrubino
 */
public class PythonResolver {

    private PythonResolver() {
    }

    @Nullable
    public static PyClass findClass(String name, Project project) {
        Collection<PyClass> classes = safeFind(name, project);
        if (classes == null || classes.isEmpty()) {
            return safeFindClass(name, project);
        }
        // TODO: what if there is more than one class found?
        return classes.iterator().next();
    }

    private static PyClass safeFindClass(String name, Project project) {
        try {
            return PyClassNameIndex.findClass(name, project);
        } catch (NullPointerException e) {
            e.printStackTrace();
            // seems to happen if python plugin dependency is not right in this project
            return null;
        } catch (ClassCastException e) {
            e.printStackTrace();
            // seems to happen if python plugin dependency is not right in this project
            return null;
        }
    }

    private static Collection<PyClass> safeFind(String name, Project project) {
        try {
            return PyClassNameIndex.find(name, project, true);
        } catch (NullPointerException e) {
            e.printStackTrace();
            // seems to happen if python plugin dependency is not right in this project
            return Collections.emptyList();
        } catch (ClassCastException e) {
            e.printStackTrace();
            // seems to happen if python plugin dependency is not right in this project
            return Collections.emptyList();
        }
    }
}
