package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.KeywordInvokable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class RobotKeywordNotFound extends SimpleRobotInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.message("INSP.NAME.keyword.undefined");
    }

    @Override
    public boolean skip(PsiElement element) {
        if (element.getNode().getElementType() != RobotTokenTypes.KEYWORD) {
            return true;
        } else if (element instanceof KeywordInvokable) {
            // this is to skip the whole keyword as opposed to just the text
            return true;
        }

        String text = element.getText();
        if (text.startsWith("$")) {
            // TODO: variable declarations
            return true;
        } else if (text.startsWith("@")) {
            // TODO: variable declarations
            return true;
        } else if (text.startsWith(":")) {
            // TODO: for loops
            return true;
        } else if (text.startsWith("\\")) {
            // TODO: for loops
            return true;
        }

        PsiElement parent = element.getParent();
        if (!(parent instanceof KeywordInvokable)) {
            return false;
        }
        PsiReference reference = parent.getReference();
        return reference != null && reference.resolve() != null;
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.keyword.undefined");
    }
}
