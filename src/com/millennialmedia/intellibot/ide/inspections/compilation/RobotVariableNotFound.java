package com.millennialmedia.intellibot.ide.inspections.compilation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.ide.inspections.SimpleRobotInspection;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.*;
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
        if (element.getNode().getElementType() != RobotTokenTypes.VARIABLE) {
            return true;
        }

        PsiElement parent = element.getParent();
        if (parent instanceof Variable) {
            PsiReference reference = parent.getReference();
            if (reference != null && reference.resolve() != null) {
                return true;
            }
        } else {
            return true;
        }

        // TODO: what is needed below this point...
        parent = element.getParent().getParent();
        if (parent instanceof Argument) {
            PsiElement container = parent.getParent();
            if (container instanceof BracketSetting) {
                // these contain variable declarations
                return true;
            }
            if (container instanceof KeywordStatement) {
                KeywordInvokable invokable = ((KeywordStatement) container).getInvokable();
                String text = invokable == null ? null : invokable.getPresentableText();
                if (text != null) {
                    if (text.startsWith(":")) {
                        // TODO: for loops
                        return true;
                    } else if (text.startsWith("\\")) {
                        // TODO: for loops
                        return true;
                    }
                }
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
        }
        return false;
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.variable.undefined");
    }
}
