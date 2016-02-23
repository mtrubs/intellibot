package com.millennialmedia.intellibot.psi.ref;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.*;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformUtils;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author mrubino
 */
public class PythonResolver {

    private static final String PYTHON_PLUGIN_U = "Pythonid";
    private static final String PYTHON_PLUGIN_CE = "PythonCore";
    private static Boolean hasPython;

    private PythonResolver() {
        NotificationsConfiguration.getNotificationsConfiguration().register(
                "intellibot.debug", NotificationDisplayType.NONE);
    }

    @Nullable
    public static PyClass castClass(@Nullable PsiElement element) {
        if (element != null && hasPython(element.getProject())) {
            if (element instanceof PyClass) {
                return (PyClass) element;
            }
        }
        return null;
    }

    @Nullable
    public static PyFile castFile(@Nullable PsiElement element) {
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
        String shortName = getShortName(name);
        Collection<PyClass> classes = safeFind(shortName, project);
        PyClass matchedByName = null;
        if (classes != null) {
            for (PyClass pyClass : classes) {
                String qName = pyClass.getQualifiedName();
                if (qName != null && qName.equals(name)) {
                    return pyClass;
                }
                // save last match on full name should qualified name never match
                String className = pyClass.getName();
                if (className != null && className.equals(name)) {
                    matchedByName = pyClass;
                }
            }
        }
        return matchedByName;
    }

    @NotNull
    private static String getShortName(@NotNull String name) {
        int pos = name.lastIndexOf(".");
        return pos > 0 ? name.substring(pos + 1) : name;
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
        } else {
            String pluginId = PlatformUtils.isCommunityEdition() ? PYTHON_PLUGIN_CE : PYTHON_PLUGIN_U;
            PluginId pythonPluginId = PluginId.getId(pluginId);
            IdeaPluginDescriptor pythonPlugin = PluginManager.getPlugin(pythonPluginId);
            if (pythonPlugin != null && pythonPlugin.isEnabled()) {
                debug(project, String.format("python support enabled by '%s'", pluginId));
                return true;
            }
            debug(project, String.format("no python support found, '%s' is not present/enabled.", pluginId));
            if (PlatformUtils.isIntelliJ()) {
                Notifications.Bus.notify(new Notification("intellibot.python",
                        RobotBundle.message("plugin.python.missing.title"),
                        RobotBundle.message("plugin.python.missing"),
                        NotificationType.WARNING));
            }
            return false;
        }
    }

    private static void debug(Project project, String message) {
        if (RobotOptionsProvider.getInstance(project).isDebug()) {
            message = String.format("[PythonResolver][plugins] %s", message);
            Notifications.Bus.notify(new Notification("intellibot.debug", "Debug", message, NotificationType.INFORMATION));
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
