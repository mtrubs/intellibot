/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author mrubino
 */
public class RobotFeatureFileType extends LanguageFileType {

    public static final RobotFeatureFileType INSTANCE = new RobotFeatureFileType();

    protected RobotFeatureFileType() {
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
        return "feature";
    }

    @Nullable
    public Icon getIcon() {
        return AllIcons.FileTypes.Properties;
    }
}
