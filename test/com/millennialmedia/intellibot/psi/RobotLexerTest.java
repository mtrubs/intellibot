package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;
import com.millennialmedia.intellibot.ResourceLoader;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import static com.millennialmedia.intellibot.psi.RobotLexer.*;

/**
 * @author mrubino
 */
@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class RobotLexerTest extends TestCase {

    private int maxState = -1;

    @Test
    public void testState() {
        // translate the state back and forth a couple thousand ways
        for (int i = 0; i < 50000; i++) {
            Assert.assertEquals(RobotLexer.toState(RobotLexer.fromState(i)), i);
        }

        Stack<Integer> gold = new Stack<Integer>();
        gold.push(1);
        gold.push(1);
        gold.push(8);
        gold.push(10);
        gold.push(5);
        int state = RobotLexer.toState(gold);
        Assert.assertEquals(122125, state);
        Stack<Integer> parsed = RobotLexer.fromState(state);
        Assert.assertEquals(gold.size(), parsed.size());
        while (!gold.isEmpty()) {
            Assert.assertEquals(gold.pop(), parsed.pop());
        }
    }

    @Test
    public void testParsingTestData() {
        runLexer(11906);
    }

    @Test
    public void testDemo() {
        runLexer(1682);
    }

    @Test
    public void testEmptyHeaders() {
        runLexer(963);
    }

    @Test
    public void testJunk() {
        runLexer(0);
    }

    @Test
    public void testVariables() {
        runLexer(11763);
    }

    @Test
    public void testParse() {
        this.maxState = -1;
        String data = getData(ResourceLoader.getResourcePath("samples/ParsingTestData.robot"));


        RobotLexer lexer = new RobotLexer(RobotKeywordProvider.getInstance());
        lexer.start(data);
        assertState(lexer, "documentation", RobotTokenTypes.COMMENT, NONE);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, NONE);
        lexer.advance();
        assertState(lexer, "this is a sample file", RobotTokenTypes.COMMENT, NONE);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, NONE);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, NONE);
        lexer.advance();
        assertState(lexer, "*** Settings ***", RobotTokenTypes.HEADING, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "# fun things going on here", RobotTokenTypes.COMMENT, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Documentation", RobotTokenTypes.SETTING, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Test the account dashboard", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "...", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "...", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "and this goes to the next line", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Documentation", RobotTokenTypes.SETTING, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "...", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "all new line", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/db_advertiser_actions.robot", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/db_campaign_actions.robot", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/db_staff_actions.robot", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/ui_login_page.robot", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/ui_manage_accounts_page.robot", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle_db_cleanup/kyle_cleanup.robot", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Library", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Selenium2Library", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "timeout=", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "${ENV['selenium']['timeout']}", RobotTokenTypes.VARIABLE, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "implicit_wait=", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "${ENV['selenium']['implicit_wait']}", RobotTokenTypes.VARIABLE, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Library", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "db.orm.Orm", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Library", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "OperatingSystem", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Library", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Collections", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Force Tags", RobotTokenTypes.SETTING, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Kyle", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Advertiser", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Dashboard", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Component", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "#Other Tag", RobotTokenTypes.COMMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Suite Teardown", RobotTokenTypes.SETTING, SYNTAX);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, SYNTAX);
        lexer.advance();
        assertState(lexer, "This works", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "*** Variables ***", RobotTokenTypes.HEADING, VARIABLES_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, VARIABLES_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, VARIABLES_HEADING);
        lexer.advance();
        assertState(lexer, "${Total_Requests}", RobotTokenTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "97,000", RobotTokenTypes.ARGUMENT, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, VARIABLES_HEADING);
        lexer.advance();
        assertState(lexer, "${kw_timeout}", RobotTokenTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "20 sec", RobotTokenTypes.ARGUMENT, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, VARIABLES_HEADING);
        lexer.advance();
        assertState(lexer, "${kw_retry_interval}", RobotTokenTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "0.5 sec", RobotTokenTypes.ARGUMENT, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, VARIABLES_HEADING);
        lexer.advance();
        assertState(lexer, "@{some_letters}", RobotTokenTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "A", RobotTokenTypes.ARGUMENT, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "B", RobotTokenTypes.ARGUMENT, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "C", RobotTokenTypes.ARGUMENT, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, VARIABLES_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, VARIABLES_HEADING);
        lexer.advance();
        assertState(lexer, "*** Test Cases ***", RobotTokenTypes.HEADING, TEST_CASES_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, TEST_CASES_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, TEST_CASES_HEADING);
        lexer.advance();
        assertState(lexer, "Scenario: An admin can see the conversion trend", RobotTokenTypes.KEYWORD_DEFINITION, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Tags]", RobotTokenTypes.BRACKET_SETTING, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Was Flickering", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Setup]", RobotTokenTypes.BRACKET_SETTING, SYNTAX);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, SYNTAX);
        lexer.advance();
        assertState(lexer, "Prepare advertiser \"Robot_Company\"", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Given", RobotTokenTypes.GHERKIN, GHERKIN);
        lexer.advance();
        assertState(lexer, " ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Advertiser has performance data", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "And", RobotTokenTypes.GHERKIN, GHERKIN);
        lexer.advance();
        assertState(lexer, " ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "I have an Account Manager", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "MANAGER", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "When", RobotTokenTypes.GHERKIN, GHERKIN);
        lexer.advance();
        assertState(lexer, " ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "I login to Tapmatch as Staff", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "${ACCOUNT_MANAGER.user.name}", RobotTokenTypes.VARIABLE, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "${ACCOUNT_MANAGER.user.password}", RobotTokenTypes.VARIABLE, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Then", RobotTokenTypes.GHERKIN, GHERKIN);
        lexer.advance();
        assertState(lexer, " ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "The 7-day average should be", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "$0.09", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Teardown]", RobotTokenTypes.BRACKET_SETTING, SYNTAX);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, SYNTAX);
        lexer.advance();
        assertState(lexer, "Run Keywords", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Close All Browsers", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Clean Database", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, TEST_CASES_HEADING);
        lexer.advance();
        assertState(lexer, "Scenario: This is also a keyword definition", RobotTokenTypes.KEYWORD_DEFINITION, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Documentation]", RobotTokenTypes.BRACKET_SETTING, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "adding another", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "# just for fun", RobotTokenTypes.COMMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "...", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "keyword will be classified correctly", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Given", RobotTokenTypes.GHERKIN, GHERKIN);
        lexer.advance();
        assertState(lexer, " ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "this sometimes works", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Then", RobotTokenTypes.GHERKIN, GHERKIN);
        lexer.advance();
        assertState(lexer, " ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "I will be happy", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "And", RobotTokenTypes.GHERKIN, GHERKIN);
        lexer.advance();
        assertState(lexer, " ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "I will be happy", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "12", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "123", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, TEST_CASES_HEADING);
        lexer.advance();
        assertState(lexer, "*** Keywords ***", RobotTokenTypes.HEADING, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "Clean Database", RobotTokenTypes.KEYWORD_DEFINITION, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Documentation]", RobotTokenTypes.BRACKET_SETTING, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Cleans the database", RobotTokenTypes.ARGUMENT, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Arguments]", RobotTokenTypes.BRACKET_SETTING, SETTINGS);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, SETTINGS);
        lexer.advance();
        assertState(lexer, "${defined}", RobotTokenTypes.VARIABLE_DEFINITION, SETTINGS);
        lexer.advance();
        assertState(lexer, "=", RobotTokenTypes.ARGUMENT, SETTINGS);
        lexer.advance();
        assertState(lexer, "${Total_Requests}", RobotTokenTypes.VARIABLE, SETTINGS);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "${var1}", RobotTokenTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, " =", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "This works", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "1", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Clean", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Kyle", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "${var2}", RobotTokenTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "${var3}", RobotTokenTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, " =", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, VARIABLE_DEFINITION);
        lexer.advance();
        assertState(lexer, "This should work", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "2", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "${defined}", RobotTokenTypes.VARIABLE, KEYWORD);
        lexer.advance();
        assertState(lexer, " Clean", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Ike", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "#    Clean  Other Stuff", RobotTokenTypes.COMMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Close All Browsers", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "This ", RobotTokenTypes.KEYWORD_DEFINITION, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "${rate}", RobotTokenTypes.VARIABLE_DEFINITION, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, " works", RobotTokenTypes.KEYWORD_DEFINITION, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "its a new ", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "${rate}", RobotTokenTypes.VARIABLE, KEYWORD);
        lexer.advance();
        assertState(lexer, " keyword", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "run keyword if", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "${a}", RobotTokenTypes.VARIABLE, KEYWORD);
        lexer.advance();
        assertState(lexer, "=", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "${b}", RobotTokenTypes.VARIABLE, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "equal", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "not equal", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Return]", RobotTokenTypes.BRACKET_SETTING, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "${Total_Requests}", RobotTokenTypes.VARIABLE, IMPORT);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "I will be happy", RobotTokenTypes.KEYWORD_DEFINITION, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "there is a smile on my face", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "...", RobotTokenTypes.ARGUMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORDS_HEADING);
        // confirm we are at the end
        Assert.assertEquals(lexer.getTokenEnd(), lexer.getBufferEnd());
        lexer.advance();
        // just a check on how close we get to Integer.MAX_VALUE; 2147483647
        Assert.assertEquals(11906, this.maxState);
    }

    private void writeState(RobotLexer lexer, StringBuilder string) {
        CharSequence actualData = lexer.getBufferSequence().subSequence(lexer.getTokenStart(), lexer.getTokenEnd());
        int actualState = lexer.peekState();
        IElementType actualToken = lexer.getTokenType();
        String message = "\"" + actualData + "\", RobotTokenTypes." + actualToken + ", " + actualState;
        message = message.replaceAll("\n", "\\\\n");
        message = message.replaceAll("\t", "\\\\t");

        string.append(message);
        string.append("\n");

        int currentState = lexer.getState();
        if (currentState > this.maxState) {
            this.maxState = currentState;
        }
    }

    private void assertState(RobotLexer lexer, String data, RobotElementType token, int state) {
        CharSequence actualData = lexer.getBufferSequence().subSequence(lexer.getTokenStart(), lexer.getTokenEnd());
        int actualState = lexer.peekState();
        IElementType actualToken = lexer.getTokenType();
        String message = "\"" + actualData + "\", RobotTokenTypes." + actualToken + ", " + actualState;

        Assert.assertEquals("Data: " + message, data, actualData);
        Assert.assertEquals("Token: " + message, token, actualToken);
        Assert.assertEquals("State: " + message, state, actualState);
        int currentState = lexer.getState();
        if (currentState > this.maxState) {
            this.maxState = currentState;
        }
    }

    private String getTestName() {
        return StringUtil.trimStart(getName(), "test");
    }

    private void runLexer(int maxState) {
        String name = getTestName();
        this.maxState = -1;
        String data = getData(ResourceLoader.getResourcePath("samples/" + name + ".robot"));
        String expected = getData(ResourceLoader.getResourcePath("samples/" + name + ".lexer.txt"));
        StringBuilder actual = new StringBuilder();

        RobotLexer lexer = new RobotLexer(RobotKeywordProvider.getInstance());
        lexer.start(data);
        while (lexer.getTokenEnd() < lexer.getBufferEnd()) {
            writeState(lexer, actual);
            lexer.advance();
        }
        writeState(lexer, actual);
        lexer.advance();
        writeState(lexer, actual);
        // just a check on how close we get to Integer.MAX_VALUE; 2147483647
        Assert.assertEquals(maxState, this.maxState);
        Assert.assertEquals(expected, actual.toString());
    }

    private String getData(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString().replace("\r", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
