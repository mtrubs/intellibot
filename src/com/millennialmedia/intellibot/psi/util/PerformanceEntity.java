package com.millennialmedia.intellibot.psi.util;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2014-06-27
 */
public interface PerformanceEntity {

    @NotNull
    String getDebugFileName();

    @NotNull
    String getDebugText();

    @NotNull
    Project getProject();
}
