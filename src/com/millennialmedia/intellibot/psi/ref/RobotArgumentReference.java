package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.python.PythonFileType;
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
            Import importElement = (Import) parent;
            if (importElement.isResource()) {
                return resolveResource();
            } else if (importElement.isLibrary()) {
                return resolveLibrary();
            }
        }
        // TODO: handle other argument types
        return null;
    }

    private PsiElement resolveLibrary() {
        String library = getElement().getPresentableText();

        // TODO: figure out where the library is in the python path
        PythonFileType fileType = PythonFileType.INSTANCE;

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

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
