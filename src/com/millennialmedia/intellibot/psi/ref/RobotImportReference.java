package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.millennialmedia.intellibot.psi.element.Import;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Scott Albertine
 */
public class RobotImportReference extends PsiReferenceBase<Import> {

    public RobotImportReference(Import element, boolean soft) {
        super(element, soft);
    }

    public RobotImportReference(@NotNull Import element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
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
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
