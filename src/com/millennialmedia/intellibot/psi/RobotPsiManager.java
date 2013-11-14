/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.AbstractModificationTracker;
import com.intellij.psi.impl.PsiManagerImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class RobotPsiManager extends AbstractModificationTracker implements ProjectComponent {

    public RobotPsiManager(PsiManagerImpl psiManager) {
        super(psiManager);
    }

    public static RobotPsiManager getInstance(@NotNull final Project project) {
        return project.getComponent(RobotPsiManager.class);
    }

    @Override
    protected boolean isInsideCodeBlock(PsiElement element) {
        // TODO: MTR
        return false;
    }

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
