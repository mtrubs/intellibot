package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.millennialmedia.intellibot.RobotBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public abstract class RobotInspection extends LocalInspectionTool { // implements CustomSuppressableInspectionTool {

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return RobotBundle.message("INSP.GROUP.robot");
    }

    @NotNull
    @Override
    public String getShortName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

}
