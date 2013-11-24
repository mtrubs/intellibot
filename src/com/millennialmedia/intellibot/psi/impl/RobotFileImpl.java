package com.millennialmedia.intellibot.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.millennialmedia.intellibot.psi.*;
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
        return RobotFileType.INSTANCE;
    }

    @Override
    public List<String> getKeywords() {

        PsiElement[] children = getChildren();

        List<String> result = new ArrayList<String>();
        for (PsiElement child : getChildren()) {
            if (child instanceof Heading) {
                for (PsiElement headingChild : child.getChildren()) {
                    if (headingChild instanceof KeywordDefinition)
                        result.add(((KeywordDefinition) headingChild).getPresentableText());
                }
            }
        }
        return result;
    }
}
