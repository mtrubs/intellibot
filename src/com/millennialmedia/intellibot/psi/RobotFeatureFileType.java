package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author mrubino
 */
public class RobotFeatureFileType extends LanguageFileType {

    private static final Icon ROBOT_ICON = IconLoader.findIcon("/images/robot_icon.png");
    private static final RobotFeatureFileType INSTANCE = new RobotFeatureFileType();

    public static RobotFeatureFileType getInstance() {
        return INSTANCE;
    }

    private RobotFeatureFileType() {
        super(RobotLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "Robot Feature";
    }

    @NotNull
    public String getDescription() {
        return "Robot Feature Files";
    }

    @NotNull
    public String getDefaultExtension() {
        return "robot";
    }

    @Nullable
    public Icon getIcon() {
        return ROBOT_ICON;
    }
}
