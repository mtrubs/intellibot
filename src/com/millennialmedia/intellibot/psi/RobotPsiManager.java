package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class RobotPsiManager implements ProjectComponent {

    public void projectOpened() {
        // nothing
    }

    public void projectClosed() {
        // nothing
    }

    public void initComponent() {
        // nothing
    }

    public void disposeComponent() {
        // nothing
    }

    @NotNull
    public String getComponentName() {
        return "RobotPsiManager";
    }
}
