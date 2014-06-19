package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.millennialmedia.intellibot.psi.element.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Scott Albertine
 */
public class RobotArgumentReference extends PsiReferenceBase<Argument> {

    public RobotArgumentReference(@NotNull Argument element) {
        this(element, false);
    }

    private RobotArgumentReference(Argument element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement parent = getElement().getParent();
        if (parent instanceof Import) {
            Import importElement = (Import) parent;
            if (importElement.isResource()) {
                return resolveResource();
            } else if (importElement.isLibrary() || importElement.isVariables()) {
                return resolveLibrary();
            }
        } else if (parent instanceof KeywordStatement) {
            PsiElement reference = resolveKeyword();
            return reference == null ? resolveVariable() : reference;
        }
        // TODO: handle other argument types
        return null;
    }

    private PsiElement resolveKeyword() {
        Argument element = getElement();
        String keyword = element.getPresentableText();
        // all files we import are based off the file we are currently in
        return ResolverUtils.resolveKeywordFromFile(keyword, element.getContainingFile());
    }

    private PsiElement resolveVariable() {
        PsiFile file = getElement().getContainingFile();
        String text = getElement().getPresentableText();
        if (file instanceof RobotFile) {
            // TODO set_test_variable, set_suite_variable, set_global_variable
            // TODO prior keywords defining
            // TODO keyword definition/arguments
            Collection<DefinedVariable> fileVariables = ((RobotFile) file).getDefinedVariables();
            for (DefinedVariable variable : fileVariables) {
                if (variable.matches(text)) {
                    return variable.reference();
                }
            }
        }
        return null;
    }

    private PsiElement resolveLibrary() {
        String library = getElement().getPresentableText();
        if (library == null) {
            return null;
        }
        PsiElement result = PythonResolver.findClass(library, myElement.getProject());
        if (result != null) {
            return result;
        }

        library = library.replace(".py", "").replaceAll("\\.", "\\/");
        while (library.contains("//")) {
            library = library.replace("//", "/");
        }
        String[] file = getFilename(library, ".py");
        // search project scope
        result = findFile(file[0], file[1], ProjectScope.getContentScope(this.myElement.getProject()));
        if (result != null) {
            return result;
        }
        // search global scope... this can get messy
        result = findFile(file[0], file[1], GlobalSearchScope.allScope(this.myElement.getProject()));
        if (result != null) {
            return result;
        }
        return null;
    }

    private PsiElement resolveResource() {
        String resource = getElement().getPresentableText();
        if (resource == null) {
            return null;
        }
        String[] file = getFilename(resource, "");
        return findFile(file[0], file[1], GlobalSearchScope.allScope(this.myElement.getProject()));
    }

    @Nullable
    private PsiFile findFile(@NotNull String path, @NotNull String filename, @NotNull GlobalSearchScope search) {
        PsiFile[] files = FilenameIndex.getFilesByName(this.myElement.getProject(), filename, search);
        StringBuilder builder = new StringBuilder();
        builder.append(path);
        builder.append(filename);
        path = builder.reverse().toString();
        for (PsiFile file : files) {
            if (acceptablePath(path, file)) {
                return file;
            }
        }
        return null;
    }

    private boolean acceptablePath(@NotNull String path, @Nullable PsiFile file) {
        if (file == null) {
            return false;
        }
        String filePath = new StringBuilder(file.getVirtualFile().getCanonicalPath()).reverse().toString();
        return filePath.startsWith(path);
    }

    @NotNull
    private static String[] getFilename(@NotNull String path, @NotNull String suffix) {
        String[] pathElements = path.split("/");
        String result;
        if (pathElements.length == 0) {
            result = path;
        } else {
            result = pathElements[pathElements.length - 1];
        }
        String[] results = new String[2];
        results[0] = path.replace(result, "");
        if (!result.toLowerCase().endsWith(suffix.toLowerCase())) {
            result += suffix;
        }
        results[1] = result;
        return results;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
