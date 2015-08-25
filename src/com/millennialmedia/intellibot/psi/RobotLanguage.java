package com.millennialmedia.intellibot.psi;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class RobotLanguage extends Language {

    public static final RobotLanguage INSTANCE = new RobotLanguage();

    private RobotLanguage() {
        super("Robot", "");
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Robot";
    }
}
