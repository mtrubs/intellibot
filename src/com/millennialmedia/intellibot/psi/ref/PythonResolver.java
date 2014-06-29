package com.millennialmedia.intellibot.psi.ref;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectImpl;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformUtils;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author mrubino
 */
public class PythonResolver {

    private static Boolean hasPython;

    private PythonResolver() {
        Notifications.Bus.register("intellibot.debug", NotificationDisplayType.NONE);
    }

    public static PyClass castClass(PsiElement element) {
        if (element != null && hasPython(element.getProject())) {
            if (element instanceof PyClass) {
                return (PyClass) element;
            }
        }
        return null;
    }

    public static PyFile castFile(PsiElement element) {
        if (element != null && hasPython(element.getProject())) {
            if (element instanceof PyFile) {
                return (PyFile) element;
            }
        }
        return null;
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

    private static final Collection<String> PYTHON_PLUGINS = new HashSet<String>();

    static {
        PYTHON_PLUGINS.add("Pythonid");
        PYTHON_PLUGINS.add("PythonCore");
    }

    private static synchronized boolean hasPython(Project project) {
        if (hasPython == null) {
            hasPython = detectPython(project);
        }
        return hasPython;
    }

    private static boolean detectPython(Project project) {
        if (PlatformUtils.isPyCharm()) {
            return true;
        } else if (project instanceof ProjectImpl) {
            Collection<String> plugins = new HashSet<String>();
            for (ComponentConfig component : ((ProjectImpl) project).getComponentConfigurations()) {
                String pluginId = component.getPluginId().getIdString();
                plugins.add(pluginId);
                if (PYTHON_PLUGINS.contains(pluginId)) {
                    debug(project, "python support enabled by: " + pluginId);
                    return true;
                }
            }
            debug(project, "no python support found: " + plugins.toString());
            if (PlatformUtils.isIntelliJ()) {
                Notifications.Bus.notify(new Notification("intellibot.python",
                        RobotBundle.message("plugin.python.missing.title"),
                        RobotBundle.message("plugin.python.missing"),
                        NotificationType.WARNING));
            }
        }
        return false;
    }

    private static void debug(Project project, String message) {
        if (RobotOptionsProvider.getInstance(project).isDebug()) {
            message = String.format("[PythonResolver][plugins] %s", message);
            Notifications.Bus.notify(new Notification("intellibot.debug", "Debug", message, NotificationType.INFORMATION));
        }
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
