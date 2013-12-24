package com.millennialmedia.intellibot.psi.ref;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.millennialmedia.intellibot.psi.element.Argument;
import com.millennialmedia.intellibot.psi.element.Import;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Scott Albertine
 */
public class RobotArgumentReference extends PsiReferenceBase<Argument> {

    public RobotArgumentReference(@NotNull Argument element) {
        this(element, false);
    }

    public RobotArgumentReference(Argument element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement parent = getElement().getParent();
        if (parent instanceof Import) {
            if (((Import) parent).isResource()) {
                return resolveResource();
            }
            // TODO: libraries
        }
        // TODO: handle other argument types
        return null;
    }

    private PsiElement resolveResource() {
        String filePath = getElement().getPresentableText();
        String[] pathElements = filePath.split("/"); //TODO: crude, we want to look up the actual file in the future, this is quick and dirty
        PsiFile[] files = FilenameIndex.getFilesByName(myElement.getProject(), pathElements[pathElements.length - 1], GlobalSearchScope.allScope(myElement.getProject()));
        if (files.length > 0) {
            return files[0];
        }
        return null;
    }

    private VirtualFile getImportFile(String importPath) {
        // TODO: nothing seems to want to work here...
        VirtualFile a = VirtualFileManager.getInstance().getFileSystem(LocalFileSystem.PROTOCOL).findFileByPath(importPath);
//        if (importPath == null) {
//            return null;
//        } else
        if (!importPath.startsWith("/")) {
            importPath = "/" + importPath;
        }
        VirtualFile b = VirtualFileManager.getInstance().getFileSystem(LocalFileSystem.PROTOCOL).findFileByPath(importPath);
        return LocalFileSystem.getInstance().findFileByPath(importPath);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
