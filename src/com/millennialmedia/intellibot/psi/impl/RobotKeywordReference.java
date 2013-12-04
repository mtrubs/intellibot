package com.millennialmedia.intellibot.psi.impl;

import java.util.HashSet;
import java.util.Set;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.millennialmedia.intellibot.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 */
public class RobotKeywordReference extends PsiReferenceBase<KeywordInvokable> {

    private static final Object[] EMPTY = {};

    public RobotKeywordReference(@NotNull KeywordInvokable element) {
        this(element, false);
    }

    public RobotKeywordReference(KeywordInvokable element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        // TODO: better way to search for these files
        // TODO: better way to search for matches in each file

        KeywordInvokable element = getElement();
        //strip text to just the main keyword definition, ignoring the arguments.  This is done outside the loop for efficiency's sake.
        String actualKeyword = element.getPresentableText();

        //get files we can import from, including our own
        RobotFile currentFile = PsiTreeUtil.getParentOfType(getElement(), RobotFile.class);
        Set<PsiFile> importedFiles = new HashSet<PsiFile>();
        importedFiles.add(currentFile); //add our own file, since we could use keywords in it too

        if (currentFile != null) {
            Heading[] headings = PsiTreeUtil.getChildrenOfType(currentFile, Heading.class);
            if (headings != null) {
                for (Heading heading : headings) {
                    if (heading.containsImports()) {
                        Import[] imports = PsiTreeUtil.getChildrenOfType(heading, Import.class);
                        for (Import eachImport : imports) {
                            String[] path = eachImport.getLastChild().getText().split("/"); //TODO: crude, we want to look up the actual file in the future, this is quick and dirty
                            PsiFile[] files = FilenameIndex.getFilesByName(myElement.getProject(), path[path.length - 1], GlobalSearchScope.allScope(myElement.getProject()));
                            for (PsiFile psiFile : files) {
                                importedFiles.add(psiFile);
                            }
                        }
                    }
                }
            }
        }

        //find the actual keyword to link to
        for (PsiFile file : importedFiles) {
            if (file != null) {
                Heading[] headings = PsiTreeUtil.getChildrenOfType(file, Heading.class);
                if (headings != null) {
                    for (Heading heading : headings) {
                        if (heading.containsKeywordDefinitions()) {
                            KeywordDefinition[] definitions = PsiTreeUtil.getChildrenOfType(heading, KeywordDefinition.class);
                            if (definitions != null) {
                                for (KeywordDefinition definition : definitions) {
                                    if (definition.matches(actualKeyword)) {
                                        return definition;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        // TODO: does this come into play with inline arguments? I bet you it does
        return EMPTY;
    }
}
