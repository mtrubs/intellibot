package com.millennialmedia.intellibot.psi.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author mrubino
 * @since 2014-06-19
 */
public enum ImportType {

    RESOURCE,
    LIBRARY,
    VARIABLES,
    UNKNOWN;

    private static final Map<String, ImportType> MAP;

    static {
        MAP = new TreeMap<String, ImportType>(String.CASE_INSENSITIVE_ORDER);
        MAP.put("resource", RESOURCE);
        MAP.put("resources", RESOURCE);
        MAP.put("library", LIBRARY);
        MAP.put("libraries", LIBRARY);
        MAP.put("variable", VARIABLES);
        MAP.put("variables", VARIABLES);
    }

    @NotNull
    public static ImportType getType(@Nullable String text) {
        ImportType result = MAP.get(text == null ? null : text.trim());
        return result == null ? UNKNOWN : result;
    }
}
