package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Stephen Abrams
 */
public class RobotFileType extends LanguageFileType {
    public static final RobotFileType INSTANCE = new RobotFileType();

    protected RobotFileType() {
        super(RobotLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "Cucumber";
    }

    @NotNull
    public String getDescription() {
        return "Cucumber scenario files";
    }

    @NotNull
    public String getDefaultExtension() {
        return "feature";
    }

    public Icon getIcon() {
        return null; // todo: need robot icon
//        return icons.CucumberIcons.Cucumber;
    }
}
