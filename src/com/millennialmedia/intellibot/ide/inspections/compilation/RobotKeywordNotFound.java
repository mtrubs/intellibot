package com.millennialmedia.intellibot.ide.inspections.compilation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.ide.inspections.SimpleRobotInspection;
import com.millennialmedia.intellibot.psi.element.KeywordInvokable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

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

    private static final Pattern SKIP_PATTERN = Pattern.compile("IN(?: (?:RANGE|ENUMERATE|ZIP))?");

    @Override
    public boolean skip(PsiElement element) {
        if (element instanceof KeywordInvokable) {
            PsiReference reference = element.getReference();
            if (reference != null && reference.resolve() != null) {
                return true;
            }

            String text = ((KeywordInvokable) element).getPresentableText();
            if (SKIP_PATTERN.matcher(text).matches())
                return true;
//            if (text.startsWith(":")) {
//                // TODO: for loops
//                return true;
//            } else if (text.startsWith("\\")) {
//                // TODO: for loops
//                return true;
//            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.keyword.undefined");
    }

    @NotNull
    @Override
    protected String getGroupNameKey() {
        return "INSP.GROUP.compilation";
    }
}
