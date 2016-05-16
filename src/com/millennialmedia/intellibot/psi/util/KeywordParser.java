package com.millennialmedia.intellibot.psi.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordParser {
    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?\\})(.*)");
    private static final String ANY = ".*?";
    private static final String DOT = ".";

    @NotNull
    static public String buildPattern(@NotNull String namespace, @NotNull String python_function_name) {
        Matcher matcher = PATTERN.matcher(python_function_name);
        String result = "";
        if (matcher.matches()) {
            String start = matcher.group(1);
            String end = buildPattern("", matcher.group(3));

            if (start.length() > 0) {
                result = Pattern.quote(start);
            }
            result += ANY;
            if (end.length() > 0) {
                result += end;
            }
        } else {
            result = !python_function_name.isEmpty() ? Pattern.quote(python_function_name) : python_function_name;
        }

        if (!namespace.isEmpty()) {
            result = "(" + Pattern.quote(namespace + DOT) + ")?" + result;
        }
        return result;
    }
}
