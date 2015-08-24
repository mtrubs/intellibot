package com.millennialmedia.intellibot.ide.inspections.cleanup;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.ide.inspections.SimpleRobotInspection;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.Argument;
import com.millennialmedia.intellibot.psi.element.Import;
import com.millennialmedia.intellibot.psi.element.RobotFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class RobotImportNotUsed extends SimpleRobotInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.message("INSP.NAME.import.unused");
    }

    @Override
    public boolean skip(PsiElement element) {
        if (element.getNode().getElementType() != RobotTokenTypes.ARGUMENT) {
            return true;
        } else if (!(element instanceof Argument)) {
            return true;
        }
        PsiFile file = element.getContainingFile();
        if (!(file instanceof RobotFile)) {
            return true;
        }

        PsiElement parent = element.getParent();

        if (parent instanceof Import) {
            if (!((Import) parent).isResource()) {
                // TODO: python libraries
                // TODO: variables
                return true;
            }
            PsiElement[] children = parent.getChildren();
            // first child seems to be different than this
            if (children.length > 0 && children[0] == element) {
                PsiReference reference = element.getReference();
                if (reference == null) {
                    return true;
                }
                PsiElement importFile = reference.resolve();
                if (importFile == null) {
                    return true; // we cannot find the file thus we do not know if we use it
                }
                
                Collection<PsiFile> referenced = ((RobotFile) file).getFilesFromInvokedKeywordsAndVariables();
                return referenced.contains(importFile.getContainingFile());
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.import.unused");
    }

    @NotNull
    @Override
    protected String getGroupNameKey() {
        return "INSP.GROUP.cleanup";
    }
}
