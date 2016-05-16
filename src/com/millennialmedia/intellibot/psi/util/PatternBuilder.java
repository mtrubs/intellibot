package com.millennialmedia.intellibot.psi.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternBuilder {
    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?\\})(.*)");
    private static final String ANY = ".*?";
    private static final String DOT = ".";

    @NotNull
    static private String parseFunction(@NotNull String keyword) {
        Matcher matcher = PATTERN.matcher(keyword);
        String result = "";
        if (matcher.matches()) {
            String start = matcher.group(1);
            String end = parseFunction(matcher.group(3));

            if (start.length() > 0) {
                result = Pattern.quote(start);
            }
            result += ANY;
            if (end.length() > 0) {
                result += end;
            }
        } else {
            result = !keyword.isEmpty() ? Pattern.quote(keyword) : keyword;
        }
        return result;
    }

    @NotNull
    static private String parseNamespace(@NotNull String namespace) {
        String result = "";
        if (!namespace.isEmpty()) {
            result = "(" + Pattern.quote(namespace + DOT) + ")?";
        }
        return result;
    }


    @NotNull
    static public String parseNamespaceKeyword(@NotNull String namespace, @NotNull String keyword) {
        return parseNamespace(namespace) + parseFunction(keyword);
    }
}
