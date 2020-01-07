package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.AbstractModificationTracker;
import com.intellij.psi.impl.PsiManagerImpl;
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
