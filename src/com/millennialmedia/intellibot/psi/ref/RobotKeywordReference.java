package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.millennialmedia.intellibot.psi.element.KeywordInvokable;
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
        String keyword = element.getPresentableText();
        // all files we import are based off the file we are currently in
        return ResolverUtils.resolveKeywordFromFile(keyword, element.getContainingFile());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
