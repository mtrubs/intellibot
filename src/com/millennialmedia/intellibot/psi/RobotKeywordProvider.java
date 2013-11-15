package com.millennialmedia.intellibot.psi;

import java.util.*;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class RobotKeywordProvider {
    public static RobotKeywordTable DEFAULT_KEYWORD_TABLE = new RobotKeywordTable();
    public static Map<String, IElementType> DEFAULT_KEYWORDS = new HashMap<String, IElementType>();
    public static Set<String> globalSettings = new HashSet<String>();
    public static Set<String> testCaseSettings = new HashSet<String>();
    public static Set<String> keywordSettings = new HashSet<String>();
    public static Set<String> settingsFollowedByKeywords = new HashSet<String>();
    public static Set<String> settingsFollowedByStrings = new HashSet<String>();
    public static Set<String> keywordsWithNoSpacesAfterThem = new HashSet<String>();
    public static Set<String> keywordsWithNewlinesAfterThem = new HashSet<String>();
    public static Set<String> keywordsWithSpaceAfterThem = new HashSet<String>();
    public static Set<String> keywordsWithSuperSpaceAfterThem = new HashSet<String>();

    static {

        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Settings ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Setting ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Test Cases ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Test Case ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Keywords ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Keyword ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Metadata ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** User Keywords ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** User Keyword ***");

        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Suite Setup");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Suite Precondition");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Suite Teardown");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Suite Post Condition");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Test Timeout");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Test Setup");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Test Precondition");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Test Teardown");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Test Postcondition");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Test Template");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Documentation");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Metadata");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Force Tags");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Default Tags");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Test Timeout");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Setup");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Precondition");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Teardown");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Postcondition");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Template");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Tags");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SETTING, "Timeout");

        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.IMPORT, "Library");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.IMPORT, "Resource");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.IMPORT, "Variables");
        //DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SEPARATOR, "");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SYNTAX, "[");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SYNTAX, "]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SYNTAX, "{");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.SYNTAX, "}");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.GHERKIN, "Given");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.GHERKIN, "When");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.GHERKIN, "Then");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.GHERKIN, "And");

        DEFAULT_KEYWORD_TABLE.putAllKeywordsInto(DEFAULT_KEYWORDS);

        globalSettings.add("Suite Setup");
        globalSettings.add("Suite Precondition");
        globalSettings.add("Suite Teardown");
        globalSettings.add("Suite Postcondition");
        globalSettings.add("Test Setup");
        globalSettings.add("Test Precondition");
        globalSettings.add("Test Teardown");
        globalSettings.add("Test PostCondition");
        globalSettings.add("Test Template");
        globalSettings.add("Documentation");
        globalSettings.add("Metadata");
        globalSettings.add("Force Tags");
        globalSettings.add("Default Tags");
        globalSettings.add("Test Timeout");

        testCaseSettings.add("Setup");
        testCaseSettings.add("Precondition");
        testCaseSettings.add("Teardown");
        testCaseSettings.add("Postcondition");
        testCaseSettings.add("Template");
        testCaseSettings.add("Documentation");
        testCaseSettings.add("Tags");
        testCaseSettings.add("Timeout");

        keywordSettings.add("Teardown");
        keywordSettings.add("Documentation");
        keywordSettings.add("Arguments");
        keywordSettings.add("Return");
        keywordSettings.add("Timeout");

        settingsFollowedByKeywords.add("Suite Setup");
        settingsFollowedByKeywords.add("Suite Setup");
        settingsFollowedByKeywords.add("Suite Precondition");
        settingsFollowedByKeywords.add("Suite Teardown");
        settingsFollowedByKeywords.add("Suite Postcondition");
        settingsFollowedByKeywords.add("Test Setup");
        settingsFollowedByKeywords.add("Test Precondition");
        settingsFollowedByKeywords.add("Test Teardown");
        settingsFollowedByKeywords.add("Test PostCondition");
        settingsFollowedByKeywords.add("Test Template");
        settingsFollowedByKeywords.add("Setup");
        settingsFollowedByKeywords.add("Precondition");
        settingsFollowedByKeywords.add("Teardown");
        settingsFollowedByKeywords.add("Postcondition");
        settingsFollowedByKeywords.add("Template");

        settingsFollowedByStrings.add("Documentation");
        settingsFollowedByStrings.add("Documentation");
        settingsFollowedByStrings.add("Metadata");
        settingsFollowedByStrings.add("Force Tags");
        settingsFollowedByStrings.add("Default Tags");
        settingsFollowedByStrings.add("Test Timeout");
        settingsFollowedByStrings.add("Tags");
        settingsFollowedByStrings.add("Arguments");
        settingsFollowedByStrings.add("Return");
        settingsFollowedByStrings.add("Timeout");

        keywordsWithNoSpacesAfterThem = DEFAULT_KEYWORD_TABLE.getKeywordsOfType(RobotTokenTypes.SYNTAX);
        keywordsWithNewlinesAfterThem = DEFAULT_KEYWORD_TABLE.getKeywordsOfType(RobotTokenTypes.HEADING);
        keywordsWithSpaceAfterThem = DEFAULT_KEYWORD_TABLE.getKeywordsOfType(RobotTokenTypes.GHERKIN);
        keywordsWithSuperSpaceAfterThem = DEFAULT_KEYWORD_TABLE.getKeywordsOfTypes(RobotTokenTypes.SETTING, RobotTokenTypes.IMPORT);

    }


    public Set<String> getGlobalSettings() {
        return globalSettings;
    }

    //these require brackets around them
    public Set<String> getTestCaseSettings() {
        return testCaseSettings;
    }


    public Set<String> getKeywordSettings() {
        return keywordSettings;
    }

    public Set<String> getSettingsFollowedByKeywords() {
        return settingsFollowedByKeywords;
    }

    public Set<String> getSettingsFollowedByStrings() {
        return settingsFollowedByStrings;
    }

    public Set<String> keywordsWithNoSpacesAfterThem(String language, String keyword) {
        return keywordsWithNoSpacesAfterThem;
    }

    public Set<String> keywordsWithNewlinesAfterThem(String language, String keyword) {
        return keywordsWithNewlinesAfterThem;
    }

    public Set<String> keywordsWithSpacesAfterThem(String language, String keyword) {
        return keywordsWithSpaceAfterThem;
    }

    public Set<String> keywordsWithSuperSpacesAfterThem(String language, String keyword) {
        return keywordsWithSuperSpaceAfterThem;
    }

    //we"re good here

    public Set<String> getAllKeywords(String language) {
        return DEFAULT_KEYWORDS.keySet();
    }

    public IElementType getTokenType(String language, String keyword) {
        return DEFAULT_KEYWORDS.get(keyword);
    }

    @NotNull
    public RobotKeywordTable getKeywordsTable(@Nullable final String language) {
        return DEFAULT_KEYWORD_TABLE;
    }
}
