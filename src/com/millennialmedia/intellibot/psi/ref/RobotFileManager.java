package com.millennialmedia.intellibot.psi.ref;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This handles finding Robot files or python classes/files.
 *
 * @author mrubino
 * @since 2014-06-28
 */
public class RobotFileManager {

    private RobotFileManager() {
        Notifications.Bus.register("intellibot.debug", NotificationDisplayType.NONE);
    }

    @Nullable
    public static PsiElement findRobot(@Nullable String resource, @NotNull Project project) {
        if (resource == null) {
            return null;
        }
        String[] file = getFilename(resource, "");
        debug(resource, "Attempting global search", project);
        return findGlobalFile(resource, file[0], file[1], project);
    }

    @Nullable
    public static PsiElement findPython(@Nullable String library, @NotNull Project project) {
        if (library == null) {
            return null;
        }
        debug(library, "Attempting class search", project);
        PsiElement result = PythonResolver.findClass(library, project);
        if (result != null) {
            return result;
        }

        String mod = library.replace(".py", "").replaceAll("\\.", "\\/");
        while (mod.contains("//")) {
            mod = mod.replace("//", "/");
        }
        String[] file = getFilename(mod, ".py");
        // search project scope
        debug(library, "Attempting project search", project);
        result = findProjectFile(library, file[0], file[1], project);
        if (result != null) {
            return result;
        }
        // search global scope... this can get messy
        debug(library, "Attempting global search", project);
        result = findGlobalFile(library, file[0], file[1], project);
        if (result != null) {
            return result;
        }
        return null;
    }

    @Nullable
    private static PsiFile findProjectFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                           @NotNull Project project) {
        return findFile(original, path, fileName, project, ProjectScope.getContentScope(project));
    }

    @Nullable
    private static PsiFile findGlobalFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                          @NotNull Project project) {
        return findFile(original, path, fileName, project, GlobalSearchScope.allScope(project));
    }

    @Nullable
    private static PsiFile findFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                    @NotNull Project project, @NotNull GlobalSearchScope search) {
        debug(original, "path::" + path, project);
        debug(original, "file::" + fileName, project);
        PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, search);
        StringBuilder builder = new StringBuilder();
        builder.append(path);
        builder.append(fileName);
        path = builder.reverse().toString();
        debug(original, "matching: " + arrayToString(files), project);
        for (PsiFile file : files) {
            if (acceptablePath(path, file)) {
                debug(original, "matched: " + file.getVirtualFile().getCanonicalPath(), project);
                return file;
            }
        }
        debug(original, "no acceptable matches", project);
        return null;
    }

    private static String arrayToString(PsiFile[] files) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (files != null) {
            for (PsiFile file : files) {
                builder.append(file.getVirtualFile().getCanonicalPath());
                builder.append(";");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private static boolean acceptablePath(@NotNull String path, @Nullable PsiFile file) {
        if (file == null) {
            return false;
        }
        String filePath = new StringBuilder(file.getVirtualFile().getCanonicalPath()).reverse().toString();
        return filePath.startsWith(path);
    }

    @NotNull
    private static String[] getFilename(@NotNull String path, @NotNull String suffix) {
        // support either / or ${/}
        String[] pathElements = path.split("(\\$\\{)?/(\\})?");
        String result;
        if (pathElements.length == 0) {
            result = path;
        } else {
            result = pathElements[pathElements.length - 1];
        }
        String[] results = new String[2];
        results[0] = path.replace(result, "").replace("${/}", "/");
        if (!result.toLowerCase().endsWith(suffix.toLowerCase())) {
            result += suffix;
        }
        results[1] = result;
        return results;
    }

    private static void debug(@NotNull String lookup, String data, @NotNull Project project) {
        if (RobotOptionsProvider.getInstance(project).isDebug()) {
            String message = String.format("[RobotFileManager][%s] %s", lookup, data);
            Notifications.Bus.notify(new Notification("intellibot.debug", "Debug", message, NotificationType.INFORMATION));
        }

    }
}
