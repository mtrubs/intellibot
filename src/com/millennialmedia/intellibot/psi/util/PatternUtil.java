package com.millennialmedia.intellibot.psi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author mrubino
 * @since 2014-06-19
 */
public class PatternUtil {

    private static final Collection<String> VARIABLE_SETTERS;
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String SUPER_SPACE = "  ";
    private static final String TAB = "\t";
    private static final String NEWLINE = "\n";
    private static final String UNDERSCORE = "_";
    private static final String EQUAL = "=";
    private static final String SCALAR_START = "${";
    private static final String LIST_START = "@{";
    private static final String ENVIRONMENT_START = "%{";
    private static final String DICTIONARY_START = "&{";
    private static final String VARIABLE_CLOSE = "}";
    private static final String VARIABLE_START_PATTERN = "[\\$\\@\\%\\&]\\{";
    private static final String VARIABLE_END_PATTERN = "((\\..*?)*?(\\[.*?\\])*?)*?\\}(\\[\\d+\\])?";
    private static final String VARIABLE_SEPARATOR = "[ _]*?";

    static {
        VARIABLE_SETTERS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        VARIABLE_SETTERS.add("set test variable");
        VARIABLE_SETTERS.add("set suite variable");
        VARIABLE_SETTERS.add("set global variable");
    }

    private PatternUtil() {
    }

    @NotNull
    public static String getVariablePattern(@NotNull String original) {
        String text = original.trim();
        if (text.length() == 0) {
            return text;
        }
        // strip any equals
        if (text.endsWith(EQUAL)) {
            text = text.substring(0, text.length() - 1);
        }
        text = text.trim();
        // strip the starting marker
        if (text.startsWith(SCALAR_START) || text.startsWith(LIST_START) ||
                text.startsWith(DICTIONARY_START) || text.startsWith(ENVIRONMENT_START)) {
            text = text.substring(2);
        }
        // strip the ending marker
        if (text.endsWith(VARIABLE_CLOSE)) {
            text = text.substring(0, text.length() - 1);
        }
        if (text.isEmpty()) {
            return Pattern.quote(original);
        }
        // put it all back together allowing for ' ' or '_' optionally anywhere
        StringBuilder pattern = new StringBuilder();
        pattern.append(VARIABLE_START_PATTERN);
        for (char c : text.toCharArray()) {
            if (c == '_' || c == ' ') {
                continue;
            }
            pattern.append(VARIABLE_SEPARATOR);
            pattern.append(Pattern.quote(Character.toString(c)));
        }
        pattern.append(VARIABLE_SEPARATOR);
        pattern.append(VARIABLE_END_PATTERN);

        return pattern.toString();
    }

    public static boolean isVariableSettingKeyword(String keyword) {
        return VARIABLE_SETTERS.contains(functionToKeyword(keyword));
    }

    @Nullable
    public static String functionToKeyword(@Nullable String function) {
        return function == null ? null : function.replaceAll(UNDERSCORE, SPACE).trim();
    }

    @NotNull
    public static String getPresentableText(@Nullable String text) {
        if (text == null) {
            return EMPTY;
        }
        int newLine = indexOf(text, NEWLINE);
        int tab = indexOf(text, TAB);
        int superSpace = indexOf(text, SUPER_SPACE);

        int index = Math.min(newLine, tab);
        index = Math.min(index, superSpace);

        return text.substring(0, index).trim();
    }

    private static int indexOf(@NotNull String text, @NotNull String string) {
        int index = text.indexOf(string);
        return index < 0 ? text.length() : index;
    }
}
