package com.millennialmedia.intellibot.psi.ref;

import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectImpl;
import com.intellij.util.PlatformUtils;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author mrubino
 */
public class PythonResolver {

    private static Boolean hasPython;

    private PythonResolver() {
    }

    @Nullable
    public static PyClass findClass(String name, Project project) {
        if (!hasPython(project)) {
            return null;
        }
        Collection<PyClass> classes = safeFind(name, project);
        if (classes == null || classes.isEmpty()) {
            return safeFindClass(name, project);
        }
        // TODO: what if there is more than one class found?
        return classes.iterator().next();
    }

    private static final String PYTHON_ID = "Pythonid";

    private static synchronized boolean hasPython(Project project) {
        if (hasPython == null) {
            hasPython = detectPython(project);
        }
        return hasPython;
    }

    private static boolean detectPython(Project project) {
        // TODO: there should be a better way to figure this out
        if (PlatformUtils.isPyCharm()) {
            return true;
        } else if (project instanceof ProjectImpl) {
            for (ComponentConfig component : ((ProjectImpl) project).getComponentConfigurations()) {
                String pluginId = component.getPluginId().getIdString();
                if (PYTHON_ID.equals(pluginId)) {
                    return true;
                }
            }
        }
        return false;
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
