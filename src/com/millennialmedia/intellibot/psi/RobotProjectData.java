package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import com.millennialmedia.intellibot.psi.element.DefinedVariable;

import java.util.Collection;

/**
 * @author
 * @since 2019-10-31
 */

public class RobotProjectData {
    private Collection<DefinedVariable> GLOBAL_DEFAULT_VARIABLES = null;

    public static RobotProjectData getInstance(Project project) {
        return ServiceManager.getService(project, RobotProjectData.class);
    }

    public Collection<DefinedVariable> projectGlobalDefaultVariables() {
        return this.GLOBAL_DEFAULT_VARIABLES;
    }
    public synchronized void setProjectGlobalDefaultVariables(Collection<DefinedVariable> variables) {
        this.GLOBAL_DEFAULT_VARIABLES = variables;
    }

}
