package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.Argument;
import com.millennialmedia.intellibot.psi.element.BracketSetting;
import com.millennialmedia.intellibot.psi.element.KeywordStatement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            PsiElement container = parent.getParent();
            if (container instanceof BracketSetting) {
                // these contain variable declarations
                return true;
            }
            if (container instanceof KeywordStatement) {
                // this is the case where we have a 'set test variable' call with more than one arg
                // the first is the variable name, the second is the value
                // if there is only one argument then we might want to see where it was created
                if (((KeywordStatement) container).getGlobalVariable() != null) {
                    List<Argument> arguments = ((KeywordStatement) container).getArguments();
                    if (arguments.size() > 1 && parent == arguments.get(0)) {
                        return true;
                    }
                }
            }
            // TODO: ignore if is a 'set test variable' call && isArg1 && arg2 exists
            String text = element.getText();
            // stick to just ${variables}
            if ((text.startsWith("${") || text.startsWith("@{")) && text.endsWith("}")) {
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
