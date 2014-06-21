package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.Argument;
import com.millennialmedia.intellibot.psi.element.BracketSetting;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2014-06-18
 */
public class RobotVariableNotFound extends SimpleRobotInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.message("INSP.NAME.variable.undefined");
    }

    @Override
    public boolean skip(PsiElement element) {
        if (element.getNode().getElementType() != RobotTokenTypes.ARGUMENT) {
            return true;
        }
        PsiElement parent = element.getParent();
        if (parent instanceof Argument) {
            if (parent.getParent() instanceof BracketSetting) {
                return true;
            }
            String text = element.getText();
            // stick to just ${variables}
            if (text.startsWith("${") && text.endsWith("}")) {
                PsiReference reference = parent.getReference();
                return reference != null && reference.resolve() != null;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.variable.undefined");
    }
}
