package com.millennialmedia.intellibot.psi;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yole
 */
public class RobotKeywordProvider {

    private static RobotKeywordProvider instance = new RobotKeywordProvider();

    private RobotKeywordProvider() {
    }

    public static RobotKeywordProvider getInstance() {
        return instance;
    }


    private static final RobotKeywordTable DEFAULT_KEYWORD_TABLE = new RobotKeywordTable();
    private static final Set<String> globalSettings = new HashSet<String>();
    private static final Set<String> settingsFollowedByKeywords = new HashSet<String>();
    private static final Set<String> settingsFollowedByStrings = new HashSet<String>();

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
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Variable ***");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.HEADING, "*** Variables ***");

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

        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Setup]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Precondition]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Teardown]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Arguments]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Postcondition]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Template]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Documentation]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Tags]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Timeout]");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.BRACKET_SETTING, "[Return]");

        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.IMPORT, "Library");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.IMPORT, "Resource");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.IMPORT, "Variables");

        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.GHERKIN, "Given");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.GHERKIN, "When");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.GHERKIN, "Then");
        DEFAULT_KEYWORD_TABLE.put(RobotTokenTypes.GHERKIN, "And");

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
        settingsFollowedByKeywords.add("[Setup]");
        settingsFollowedByKeywords.add("Precondition");
        settingsFollowedByKeywords.add("[Precondition]");
        settingsFollowedByKeywords.add("Teardown");
        settingsFollowedByKeywords.add("[Teardown]");
        settingsFollowedByKeywords.add("Postcondition");
        settingsFollowedByKeywords.add("[Postcondition]");
        settingsFollowedByKeywords.add("Template");
        settingsFollowedByKeywords.add("[Template]");

        settingsFollowedByStrings.add("Documentation");
        settingsFollowedByStrings.add("[Documentation]");
        settingsFollowedByStrings.add("Metadata");
        settingsFollowedByStrings.add("Force Tags");
        settingsFollowedByStrings.add("Default Tags");
        settingsFollowedByStrings.add("Test Timeout");
        settingsFollowedByStrings.add("Tags");
        settingsFollowedByStrings.add("[Tags]");
        settingsFollowedByStrings.add("Arguments");
        settingsFollowedByStrings.add("[Arguments]");
        settingsFollowedByStrings.add("Return");
        settingsFollowedByStrings.add("[Return]");
        settingsFollowedByStrings.add("Timeout");
        settingsFollowedByStrings.add("[Timeout]");
    }

    public boolean isGlobalSetting(String word) {
        return globalSettings.contains(word);
    }

    public boolean isSyntaxFollowedByKeyword(String word) {
        return settingsFollowedByKeywords.contains(word);
    }

    public boolean isSyntaxFollowedByString(String word) {
        return settingsFollowedByStrings.contains(word);
    }

    public boolean isSyntaxOfType(RobotElementType type, String word) {
        return DEFAULT_KEYWORD_TABLE.getKeywordsOfType(type).contains(word);
    }

    public Set<String> getRecommendationsOfType(RobotElementType type) {
        // TODO: MTR: change this to be a more limited set
        return DEFAULT_KEYWORD_TABLE.getKeywordsOfType(type);
    }
}
