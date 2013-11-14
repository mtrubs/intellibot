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
public class RobotKeywordFileType extends LanguageFileType {

    public static final RobotFeatureFileType INSTANCE = new RobotFeatureFileType();

    protected RobotKeywordFileType() {
        super(RobotLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "Robot Keyword";
    }

    @NotNull
    public String getDescription() {
        return "Robot Keyword Files";
    }

    @NotNull
    public String getDefaultExtension() {
        return "rkf";
    }

    @Nullable
    public Icon getIcon() {
        return AllIcons.FileTypes.Properties;
    }
}
