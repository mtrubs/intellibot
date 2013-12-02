package com.millennialmedia.intellibot.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.millennialmedia.intellibot.psi.Heading;
import com.millennialmedia.intellibot.psi.KeywordDefinition;
import com.millennialmedia.intellibot.psi.RobotFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 */
public class RobotSimpleReference extends PsiReferenceBase<PsiElement> {

    private static final Object[] EMPTY = {};

    public RobotSimpleReference(@NotNull PsiElement element) {
        this(element, false);
    }

    public RobotSimpleReference(PsiElement element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        // TODO: better way to search for these?
        // TODO: all files in import?
        RobotFile file = PsiTreeUtil.getParentOfType(getElement(), RobotFile.class);
        if (file != null) {
            Heading[] headings = PsiTreeUtil.getChildrenOfType(file, Heading.class);
            if (headings != null) {
                for (Heading heading : headings) {
                    if (heading.containsKeywordDefinitions()) {
                        KeywordDefinition[] definitions = PsiTreeUtil.getChildrenOfType(heading, KeywordDefinition.class);
                        if (definitions != null) {
                            for (KeywordDefinition definition : definitions) {
                                if (definition.matches(getElement().getText())) {
                                    return definition;
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
        // TODO: does this come into play with inline arguments?
        return EMPTY;
    }
}
