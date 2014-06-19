package com.millennialmedia.intellibot.psi.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author mrubino
 * @since 2014-06-19
 */
public class PatternUtil {
    
    private PatternUtil() {}
    
    @NotNull
    public static String getVariablePattern(@NotNull String text) {
        text = text.trim();
        if (text.length() == 0) {
            return text;
        }
        if (text.endsWith("=")) {
            text = text.substring(0, text.length() - 1);
        }
        text = text.trim();
        if (text.startsWith("${")) {
            text = text.substring(2);
        }
        if (text.endsWith("}")) {
            text = text.substring(0, text.length() - 1);
        }
        return "\\$\\{" + Pattern.quote(text) + "((\\..*?)*?(\\[.*?\\])*?)*?\\}";
    }
}
