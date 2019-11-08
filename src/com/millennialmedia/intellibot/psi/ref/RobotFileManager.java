package com.millennialmedia.intellibot.psi.ref;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.psi.RobotProjectData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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
        debug(resource, "Attempting global robot search", project);
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
        // robotframework
        // Using library name
        //     The most common way to specify a test library to import is using its name,
        //     like it has been done in all the examples in this section. In these cases
        //     Robot Framework tries to find the class or module implementing the library
        //     from the module search path.
        if (! library.contains("/")) {
            debug(library, "Attempting class search", project);
            result = PythonResolver.findClass(library, project);
            if (result != null) {
                addToCache(result, library);
                return result;
            }
        }

        // robotframework
        // Using physical path to library
        //     Another mechanism for specifying the library to import is using a path to it
        //     in the file system. This path is considered relative to the directory where
        //     current test data file is situated similarly as paths to resource and variable
        //     files.
        //     If the library is a file, the path to it must contain extension. For Python
        //     libraries the extension is naturally .py. If Python library is implemented as
        //     a directory, the path to it must have a trailing forward slash (/) if the path
        //     is relative. With absolute paths the trailing slash is optional.
        //     A limitation of this approach is that libraries implemented as Python classes
        //     must be in a module with the same name as the class.

        boolean directory = true;
        String mod = library;
        if (library.endsWith(".py")) {
            directory = false;
            mod = mod.substring(0, mod.length()-3);
        }
        // if mod contain "/", then "." is most probably part of pathname, not to be replaced with "/"
        if (! mod.contains("/"))
            mod = mod.replaceAll("\\.", "/");
        mod = mod.replaceAll("/{2,}", "/");
        if (! directory)
            mod += ".py";
        String[] file = getFilename(mod, "", project);

        if (directory) {
            // search project scope
            debug(library, "Attempting project directory search", project);
            result = findProjectDirectory(library, file[0], file[1], project, originalElement);
            if (result != null) {
                addToCache(result, library);
                return result;
            }
            // search global scope... this can get messy
            debug(library, "Attempting global directory search", project);
            result = findGlobalDirectory(library, file[0], file[1], project, originalElement);
            if (result != null) {
                addToCache(result, library);
                return result;
            }
        } else {
            // search project scope
            debug(library, "Attempting project file search", project);
            result = findProjectFile(library, file[0], file[1], project, originalElement);
            if (result != null) {
                addToCache(result, library);
                return result;
            }
            // search global scope... this can get messy
            debug(library, "Attempting global file search", project);
            result = findGlobalFile(library, file[0], file[1], project, originalElement);
            if (result != null) {
                addToCache(result, library);
                return result;
            }
        }
        return null;
    }

    @Nullable
    private static PsiFile findProjectFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                           @NotNull Project project, @NotNull PsiElement originalElement) {
        return findFile(original, path, fileName, project, ProjectScope.getContentScope(project), originalElement, false);
    }

    @Nullable
    private static PsiFile findGlobalFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                          @NotNull Project project, @NotNull PsiElement originalElement) {
        return findFile(original, path, fileName, project, GlobalSearchScope.allScope(project), originalElement, false);
    }

    @Nullable
    private static PsiFile findProjectDirectory(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                           @NotNull Project project, @NotNull PsiElement originalElement) {
        return findFile(original, path, fileName, project, ProjectScope.getContentScope(project), originalElement, true);
    }

    @Nullable
    private static PsiFile findGlobalDirectory(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                          @NotNull Project project, @NotNull PsiElement originalElement) {
        return findFile(original, path, fileName, project, GlobalSearchScope.allScope(project), originalElement, true);
    }

    @Nullable
    private static PsiFile findFile(@NotNull String original, @NotNull String path, @NotNull String fileName,
                                    @NotNull Project project, @NotNull GlobalSearchScope search,
                                    @NotNull PsiElement originalElement, boolean directory) {
        debug(original, "path::" + path, project);
        debug(original, (directory ? "directory::" : "file::") + fileName, project);

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
        path = path + fileName;

        Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(project, fileName, search);
        debug(original, "matching: " + collectionToString(files), project);
        for (VirtualFile file : files) {
            String tryPath = file.getCanonicalPath();
            if (tryPath == null)
                continue;
            debug(original, "trying: " + tryPath, project);
            // TODO: if path is abosute path startsWith "/", endsWith comparision may return true if tryPath have prefix
            if (tryPath.endsWith(path) &&
                    ((directory && file.isDirectory()) || (!directory && !file.isDirectory()))) {
                VirtualFile targetFound = file;
                if (directory) {
                    targetFound = null;
                    for (VirtualFile sub : file.getChildren()) {
                        if (sub.getName().equals("__init__.py") && !sub.isDirectory()) {
                            targetFound = sub;
                            break;
                        }
                    }
                }
                if (targetFound != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(targetFound);
                    if (psiFile != null) {
                        debug(original, "matched: " + targetFound.getCanonicalPath(), project);
                        return psiFile;
                    }
                }
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

    private static String collectionToString(Collection<VirtualFile> files) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (files != null) {
            for (VirtualFile file : files) {
                builder.append(file.getCanonicalPath());
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
        String[] pathElements = path.split("(\\$\\{)?/(})?");
        String result;
        result = pathElements[pathElements.length - 1];
        String[] results = new String[2];
        pathElements[pathElements.length - 1] = "";
        results[0] = String.join("/", pathElements);
        // absolute or relative path
        if (! results[0].matches("([a-zA-Z]:)?/.*|.*([$%]\\{.+}).*") && ! results[0].contains("./"))
            results[0] = "./" + results[0];
        if (RobotOptionsProvider.getInstance(project).stripVariableInLibraryPath()) {
            results[0] = results[0].replaceAll("[$%]\\{|}", "");
        }
        if (!suffix.equals("") && !result.toLowerCase().endsWith(suffix.toLowerCase())) {
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
