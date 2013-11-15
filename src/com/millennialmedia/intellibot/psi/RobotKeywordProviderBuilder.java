package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * User: Andrey.Vokin
 * Date: 2/24/12
 */
public interface RobotKeywordProviderBuilder {
    ExtensionPointName<RobotKeywordProviderBuilder> EP_NAME = ExtensionPointName.create("robot.KeywordProvider");

    @Nullable
    RobotKeywordProvider getKeywordProvider(Project project);
}
