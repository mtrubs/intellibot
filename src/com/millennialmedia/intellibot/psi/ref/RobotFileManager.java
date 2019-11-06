package com.millennialmedia.intellibot.psi.ref;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.psi.RobotProjectData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This handles finding Robot files or python classes/files.
 *
 * @author mrubino
 * @since 2014-06-28
 */
public class RobotFileManager {
//    put CACHE into RobotProjectData, avoid project disposed exception when switch between project
//    private static final Map<String, PsiElement> FILE_CACHE = new HashMap<String, PsiElement>();
//    private static final MultiMap<PsiElement, String> FILE_NAMES = MultiMap.createSet();

    private RobotFileManager() {
    }

    @Nullable
    private static PsiElement getFromCache(@NotNull String value, @NotNull Project project) {
        return RobotProjectData.getInstance(project).getFromCache(value);
    }

    private static void addToCache(@Nullable PsiElement element, @NotNull String value) {
        if (element != null) {
            RobotProjectData.getInstance(element.getProject()).addToCache(element, value);
        }
    }

    @Nullable
    public static PsiElement findRobot(@Nullable String resource, @NotNull Project project,
                                       @NotNull PsiElement originalElement) {
        if (resource == null) {
            return null;
        }
        PsiElement result = getFromCache(resource, project);
        if (result != null) {
            return result;
        }
        String[] file = getFilename(resource, "", project);
        debug(resource, "Attempting global search", project);
        result = findGlobalFile(resource, file[0], file[1], project, originalElement);
        addToCache(result, resource);
        return result;
    }

    @Nullable
    public static PsiElement findPython(@Nullable String library, @NotNull Project project,
                                        @NotNull PsiElement originalElement) {
        if (library == null) {
            return null;
        }
        PsiElement result = getFromCache(library, project);
        if (result != null) {
            return result;
        }
        debug(library, "Attempting class search", project);
        result = PythonResolver.findClass(library, project);
        if (result != null) {
            addToCache(result, library);
            return result;
        }

        // only chop .py at the end
        String mod = library.replaceAll("\\.py$", "");
        // if mod contain "/", then "." is most probably part of pathname, not to be replaced with "/"
        if (! mod.contains("/"))
            mod = mod.replaceAll("\\.", "\\/");
        while (mod.contains("//")) {
            mod = mod.replace("//", "/");
        }
        String[] file = getFilename(mod, ".py", project);
        // search project scope
        debug(library, "Attempting project search", project);
        result = findProjectFile(library, file[0], file[1], project, originalElement);
        if (result != null) {
            addToCache(result, library);
            return result;
        }
        // search global scope... this can get messy
        debug(library, "Attempting global search", project);
        result = findGlobalFile(library, file[0], file[1], project, originalElement);
        if (result != null) {
            addToCache(result, library);
            return result;
        }
        return null;
    }

    @Nullable
    private static PsiFile findProjectFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                           @NotNull Project project, @NotNull PsiElement originalElement) {
        return findFile(original, path, fileName, project, ProjectScope.getContentScope(project), originalElement);
    }

    @Nullable
    private static PsiFile findGlobalFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                          @NotNull Project project, @NotNull PsiElement originalElement) {
        return findFile(original, path, fileName, project, GlobalSearchScope.allScope(project), originalElement);
    }

    @Nullable
    private static PsiFile findFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                    @NotNull Project project, @NotNull GlobalSearchScope search,
                                    @NotNull PsiElement originalElement) {
        debug(original, "path::" + path, project);
        debug(original, "file::" + fileName, project);

        if (path.contains("./")) {
            // contains a relative path
            VirtualFile workingDir = originalElement.getContainingFile().getVirtualFile().getParent();
            VirtualFile relativePath = workingDir.findFileByRelativePath(path);
            if (relativePath != null && relativePath.isDirectory() && relativePath.getCanonicalPath() != null) {
                debug(original, "changing relative path to: " + relativePath.getCanonicalPath(), project);
                path = relativePath.getCanonicalPath();
                if (!path.endsWith("/")) {
                    path += "/";
                }
            }
        }

        PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, search);
        path = path + fileName;
        debug(original, "matching: " + arrayToString(files), project);
        for (PsiFile file : files) {
            debug(original, "trying: " + file.getVirtualFile().getCanonicalPath(), project);
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
        String virtualFilePath = file.getVirtualFile().getCanonicalPath();
        if (virtualFilePath == null) {
            return false;
        }
        return virtualFilePath.endsWith(path);
    }

    @NotNull
    private static String[] getFilename(@NotNull String path, @NotNull String suffix, @NotNull Project project) {
        // support either / or ${/}
        String[] pathElements = path.split("(\\$\\{)?/(\\})?");
        String result;
        result = pathElements[pathElements.length - 1];
        String[] results = new String[2];
        pathElements[pathElements.length - 1] = "";
        results[0] = String.join("/", pathElements);
        if (RobotOptionsProvider.getInstance(project).stripVariableInLibraryPath()) {
            results[0] = results[0].replaceAll("(\\$|%)\\{|\\}", "");
        }
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
