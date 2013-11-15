package com.millennialmedia.intellibot.psi;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author: Stephen Abrams
 */
public class RobotHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> keys1;
    private static final Map<IElementType, TextAttributesKey> keys2;

    private final RobotKeywordProvider myKeywordProvider;

    public RobotHighlighter(RobotKeywordProvider keywordProvider) {
        myKeywordProvider = keywordProvider;
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new RobotLexer(myKeywordProvider);
    }

    public static final TextAttributesKey ROBOT_HEADING = TextAttributesKey.createTextAttributesKey(
            "HEADING",
            DefaultLanguageHighlighterColors.STRING
    );

    public static final TextAttributesKey ROBOT_SETTING = TextAttributesKey.createTextAttributesKey(
            "SETTING",
            DefaultLanguageHighlighterColors.KEYWORD
    );

    public static final TextAttributesKey ROBOT_BRACKET_SETTING = TextAttributesKey.createTextAttributesKey(
            "TEST_CASE_SETTING",
            DefaultLanguageHighlighterColors.LABEL
    );

    public static final TextAttributesKey ROBOT_IMPORT = TextAttributesKey.createTextAttributesKey(
            "IMPORT",
            DefaultLanguageHighlighterColors.IDENTIFIER
    );

    public static final TextAttributesKey ROBOT_TC_KW_NAME = TextAttributesKey.createTextAttributesKey(
            "TC_KW_NAME",
            DefaultLanguageHighlighterColors.KEYWORD
    );

    public static final TextAttributesKey ROBOT_KEYWORD = TextAttributesKey.createTextAttributesKey(
            "KEYWORD",
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
    );

    public static final TextAttributesKey ROBOT_ARGUMENT = TextAttributesKey.createTextAttributesKey(
            "ARGUMENT",
            DefaultLanguageHighlighterColors.PARENTHESES
    );

    public static final TextAttributesKey ROBOT_VARIABLE = TextAttributesKey.createTextAttributesKey(
            "VARIABLE",
            DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    );

    public static final TextAttributesKey ROBOT_COMMENT = TextAttributesKey.createTextAttributesKey(
            "COMMENT",
            DefaultLanguageHighlighterColors.BLOCK_COMMENT
    );

    public static final TextAttributesKey ROBOT_SEPARATOR = TextAttributesKey.createTextAttributesKey(
            "SEPARATOR",
            DefaultLanguageHighlighterColors.COMMA
    );

    public static final TextAttributesKey ROBOT_SYNTAX = TextAttributesKey.createTextAttributesKey(
            "SYNTAX",
            DefaultLanguageHighlighterColors.BRACES
    );

    public static final TextAttributesKey ROBOT_GHERKIN = TextAttributesKey.createTextAttributesKey(
            "GHERKIN",
            DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR
    );

    public static final TextAttributesKey ROBOT_ERROR = TextAttributesKey.createTextAttributesKey(
            "ERROR",
            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
    );

    static {
        keys1 = new THashMap<IElementType, TextAttributesKey>();
        keys2 = new THashMap<IElementType, TextAttributesKey>();

        keys1.put(RobotTokenTypes.HEADING, ROBOT_HEADING);
        keys1.put(RobotTokenTypes.COMMENT, ROBOT_COMMENT);
        keys1.put(RobotTokenTypes.ARGUMENT, ROBOT_ARGUMENT);
        keys1.put(RobotTokenTypes.ERROR, ROBOT_ERROR);
        keys1.put(RobotTokenTypes.GHERKIN, ROBOT_GHERKIN);
        keys1.put(RobotTokenTypes.SYNTAX, ROBOT_SYNTAX);
        keys1.put(RobotTokenTypes.SEPARATOR, ROBOT_SEPARATOR);
        keys1.put(RobotTokenTypes.VARIABLE, ROBOT_VARIABLE);
        keys1.put(RobotTokenTypes.KEYWORD, ROBOT_KEYWORD);
        keys1.put(RobotTokenTypes.TC_KW_NAME, ROBOT_TC_KW_NAME);
        keys1.put(RobotTokenTypes.BRACKET_SETTING, ROBOT_BRACKET_SETTING);
        keys1.put(RobotTokenTypes.SETTING, ROBOT_SETTING);
        keys1.put(RobotTokenTypes.IMPORT, ROBOT_IMPORT);
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(keys1.get(tokenType), keys2.get(tokenType));
    }

    public static final Map<TextAttributesKey, Pair<String, HighlightSeverity>> DISPLAY_NAMES = new THashMap<TextAttributesKey, Pair<String, HighlightSeverity>>(6);

    static {
//        DISPLAY_NAMES.put(PROPERTY_KEY, new Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.property.key"),null));
//        DISPLAY_NAMES.put(PROPERTY_VALUE, new Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.property.value"), null));
//        DISPLAY_NAMES.put(PROPERTY_KEY_VALUE_SEPARATOR, new Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.key.value.separator"), null));
//        DISPLAY_NAMES.put(PROPERTY_COMMENT, new Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.comment"), null));
//        DISPLAY_NAMES.put(PROPERTIES_VALID_STRING_ESCAPE, new Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.valid.string.escape"), null));
//        DISPLAY_NAMES.put(PROPERTIES_INVALID_STRING_ESCAPE, new Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.invalid.string.escape"), HighlightSeverity.WARNING));
    }
}
