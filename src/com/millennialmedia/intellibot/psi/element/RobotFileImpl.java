package com.millennialmedia.intellibot.psi.element;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.psi.RobotFeatureFileType;
import com.millennialmedia.intellibot.psi.RobotLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stephen Abrams
 */
public class RobotFileImpl extends PsiFileBase implements RobotFile {

    public RobotFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, RobotLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return RobotFeatureFileType.getInstance();
    }

    @Override
    public List<String> getKeywords() {
        List<String> result = new ArrayList<String>();
        for (PsiElement child : getChildren()) {
            if (child instanceof Heading) {
                if (((Heading) child).containsKeywordDefinitions()) {
                    for (PsiElement headingChild : child.getChildren()) {
                        if (headingChild instanceof KeywordDefinition)
                            result.add(((KeywordDefinition) headingChild).getPresentableText());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<RobotFile> getImportedRobotFiles() {
        List<RobotFile> robotFiles = new ArrayList<RobotFile>();
        for (PsiElement child : getChildren()) {
            if (child instanceof Heading) {
                if (((Heading) child).containsImports()) {
                    for (PsiElement headingChild : child.getChildren()) {
                        if (headingChild instanceof Import) {
                            PsiReference reference = headingChild.getReference();
                            if (reference != null) {
                                PsiElement element = reference.resolve();
                                if (element instanceof RobotFile) {
                                    robotFiles.add((RobotFile) element);
                                }
                            }
                        }
                    }
                }
            }
        }
        return robotFiles;
    }
}
