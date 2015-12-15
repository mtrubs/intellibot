package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.millennialmedia.intellibot.RobotBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public abstract class SimpleRobotInspection extends LocalInspectionTool implements SimpleInspection {

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return RobotBundle.message(getGroupNameKey());
    }

    @NotNull
    protected abstract String getGroupNameKey();

    @NotNull
    @Override
    public String getShortName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
                                          boolean isOnTheFly,
                                          @NotNull LocalInspectionToolSession session) {
        return new SimpleInspectionVisitor(holder, this);
    }
}
