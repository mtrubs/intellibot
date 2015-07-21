package com.millennialmedia.intellibot.psi;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yole
 */
public class RobotKeywordProvider {

    private static final RobotKeywordProvider INSTANCE = new RobotKeywordProvider();

    private RobotKeywordProvider() {
    }

    public static RobotKeywordProvider getInstance() {
        return INSTANCE;
    }


    private static final RobotKeywordTable KEYWORD_TABLE = new RobotKeywordTable();
    private static final Set<String> GLOBAL_SETTINGS = new HashSet<String>();
    private static final Set<String> SETTINGS_FOLLOWED_BY_KEYWORDS = new HashSet<String>();
    private static final Set<String> SETTINGS_FOLLOWED_BY_STRINGS = new HashSet<String>();
    private static final Set<String> SETTINGS_FOLLOWED_BY_VARIABLE_DEFINITIONS = new HashSet<String>();

    static {
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Settings ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Setting ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Test Cases ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Test Case ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Keywords ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Keyword ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Metadata ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** User Keywords ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** User Keyword ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Variables ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Variable ***");

        addRecommendation(RobotTokenTypes.HEADING, "*** Settings ***", "Settings");
        addRecommendation(RobotTokenTypes.HEADING, "*** Test Cases ***", "Test Cases");
        addRecommendation(RobotTokenTypes.HEADING, "*** Keywords ***", "Keywords");
        addRecommendation(RobotTokenTypes.HEADING, "*** Variables ***", "Variables");

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Suite Setup");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Suite Precondition");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Suite Teardown");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Suite Post Condition");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Timeout");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Setup");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Precondition");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Teardown");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Postcondition");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Template");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Documentation");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Metadata");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Force Tags");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Default Tags");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Timeout");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Setup");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Precondition");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Teardown");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Postcondition");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Template");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Tags");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Timeout");

        addRecommendation(RobotTokenTypes.SETTING, "Documentation", "Documentation");
        addRecommendation(RobotTokenTypes.SETTING, "Metadata", "Metadata");
        addRecommendation(RobotTokenTypes.SETTING, "Suite Setup", "Suite Setup");
        addRecommendation(RobotTokenTypes.SETTING, "Suite Teardown", "Suite Teardown");
        addRecommendation(RobotTokenTypes.SETTING, "Force Tags", "Force Tags");
        addRecommendation(RobotTokenTypes.SETTING, "Default Tags", "Default Tags");
        addRecommendation(RobotTokenTypes.SETTING, "Test Setup", "Test Setup");
        addRecommendation(RobotTokenTypes.SETTING, "Test Teardown", "Test Teardown");
        addRecommendation(RobotTokenTypes.SETTING, "Test Template", "Test Template");
        addRecommendation(RobotTokenTypes.SETTING, "Test Timeout", "Test Timeout");

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Setup]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Precondition]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Teardown]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Arguments]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Postcondition]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Template]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Documentation]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Tags]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Timeout]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Return]");

        // TODO: test case definition set
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Documentation]", "Documentation");
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Tags]", "Tags");
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Setup]", "Setup");
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Teardown]", "Teardown");
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Template]", "Template");
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Timeout]", "Timeout");

        // TODO: keyword definition set
        //addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Documentation]", "Documentation");
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Arguments]", "Arguments");
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Return]", "Return");
        //addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Teardown]", "Teardown");
        //addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Timeout]", "Timeout");

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.IMPORT, "Library");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.IMPORT, "Resource");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.IMPORT, "Variables");

        addRecommendation(RobotTokenTypes.IMPORT, "Library", "Library");
        addRecommendation(RobotTokenTypes.IMPORT, "Resource", "Resource");
        addRecommendation(RobotTokenTypes.IMPORT, "Variables", "Variables");

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "Given");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "When");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "Then");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "And");

        addRecommendation(RobotTokenTypes.GHERKIN, "Given", "Given");
        addRecommendation(RobotTokenTypes.GHERKIN, "When", "When");
        addRecommendation(RobotTokenTypes.GHERKIN, "Then", "Then");
        addRecommendation(RobotTokenTypes.GHERKIN, "And", "And");

        GLOBAL_SETTINGS.add("Suite Setup");
        GLOBAL_SETTINGS.add("Suite Precondition");
        GLOBAL_SETTINGS.add("Suite Teardown");
        GLOBAL_SETTINGS.add("Suite Postcondition");
        GLOBAL_SETTINGS.add("Test Setup");
        GLOBAL_SETTINGS.add("Test Precondition");
        GLOBAL_SETTINGS.add("Test Teardown");
        GLOBAL_SETTINGS.add("Test PostCondition");
        GLOBAL_SETTINGS.add("Test Template");
        GLOBAL_SETTINGS.add("Documentation");
        GLOBAL_SETTINGS.add("Metadata");
        GLOBAL_SETTINGS.add("Force Tags");
        GLOBAL_SETTINGS.add("Default Tags");
        GLOBAL_SETTINGS.add("Test Timeout");

        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Suite Setup");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Suite Precondition");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Suite Teardown");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Suite Postcondition");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Test Setup");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Test Precondition");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Test Teardown");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Test PostCondition");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Test Template");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Setup");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("[Setup]");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Precondition");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("[Precondition]");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Teardown");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("[Teardown]");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Postcondition");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("[Postcondition]");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Template");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("[Template]");

        SETTINGS_FOLLOWED_BY_STRINGS.add("Documentation");
        SETTINGS_FOLLOWED_BY_STRINGS.add("[Documentation]");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Metadata");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Force Tags");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Default Tags");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Test Timeout");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Tags");
        SETTINGS_FOLLOWED_BY_STRINGS.add("[Tags]");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Return");
        SETTINGS_FOLLOWED_BY_STRINGS.add("[Return]");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Timeout");
        SETTINGS_FOLLOWED_BY_STRINGS.add("[Timeout]");

        SETTINGS_FOLLOWED_BY_VARIABLE_DEFINITIONS.add("Arguments");
        SETTINGS_FOLLOWED_BY_VARIABLE_DEFINITIONS.add("[Arguments]");
    }

    private static void addRecommendation(@NotNull RobotElementType type, @NotNull String word, @NotNull String lookup) {
        KEYWORD_TABLE.addRecommendation(type, word, lookup);
    }

    public boolean isGlobalSetting(String word) {
        return GLOBAL_SETTINGS.contains(word);
    }

    public boolean isSyntaxFollowedByKeyword(String word) {
        return SETTINGS_FOLLOWED_BY_KEYWORDS.contains(word);
    }

    public boolean isSyntaxFollowedByString(String word) {
        return SETTINGS_FOLLOWED_BY_STRINGS.contains(word);
    }

    public boolean isSyntaxFollowedByVariableDefinition(String word) {
        return SETTINGS_FOLLOWED_BY_VARIABLE_DEFINITIONS.contains(word);
    }

    public boolean isSyntaxOfType(RobotElementType type, String word) {
        return KEYWORD_TABLE.getSyntaxOfType(type).contains(word);
    }
    
    @NotNull
    public Set<String> getSyntaxOfType(RobotElementType type) {
        return KEYWORD_TABLE.getSyntaxOfType(type);
    }

    @NotNull
    public Set<RecommendationWord> getRecommendationsForType(RobotElementType type) {
        return KEYWORD_TABLE.getRecommendationsForType(type);
    }
}
