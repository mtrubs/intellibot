package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import com.millennialmedia.intellibot.psi.element.KeywordInvokable;
import com.millennialmedia.intellibot.psi.element.RobotFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 */
public class RobotKeywordReference extends PsiReferenceBase<KeywordInvokable> {

    public RobotKeywordReference(@NotNull KeywordInvokable element) {
        this(element, false);
    }

    private RobotKeywordReference(KeywordInvokable element, boolean soft) {
        super(element, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        KeywordInvokable element = getElement();
        String actualKeyword = element.getPresentableText();

        // all files we import are based off the file we are currently in
        RobotFile currentFile = PsiTreeUtil.getParentOfType(getElement(), RobotFile.class);
        if (currentFile == null) {
            return null;
        }
        for (DefinedKeyword keyword : currentFile.getKeywords()) {
            if (keyword.matches(actualKeyword)) {
                return keyword.reference();
            }
        }
        for (KeywordFile file : currentFile.getImportedFiles()) {
            for (DefinedKeyword keyword : file.getKeywords()) {
                if (keyword.matches(actualKeyword)) {
                    return keyword.reference();
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
