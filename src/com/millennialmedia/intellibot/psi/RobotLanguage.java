package com.millennialmedia.intellibot.psi;

import com.intellij.lang.Language;

/**
 * @author mrubino
 */
public class RobotLanguage extends Language {

    public static RobotLanguage INSTANCE = new RobotLanguage();


    protected RobotLanguage() {
        super("Robot", "");
    }

    @Override
    public String getDisplayName() {
        return "Robot";
    }
}
