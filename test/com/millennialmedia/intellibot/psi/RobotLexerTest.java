/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import static com.millennialmedia.intellibot.psi.RobotLexer.*;

/**
 * @author mrubino
 */
public class RobotLexerTest extends TestCase {

    public void testFromState() {
        assertStack(RobotLexer.fromState(0));
        assertStack(RobotLexer.fromState(1), 1);
        assertStack(RobotLexer.fromState(4), 4);
        assertStack(RobotLexer.fromState(41), 1, 4);
        assertStack(RobotLexer.fromState(14), 4, 1);
        assertStack(RobotLexer.fromState(341), 1, 4, 3);
        assertStack(RobotLexer.fromState(431), 1, 3, 4);
        assertStack(RobotLexer.fromState(441), 1, 4, 4);
    }

    public void testToState() {
        assertEquals(RobotLexer.toState(stack()), 0);
        assertEquals(RobotLexer.toState(stack(1)), 1);
        assertEquals(RobotLexer.toState(stack(4)), 4);
        assertEquals(RobotLexer.toState(stack(1, 4)), 41);
        assertEquals(RobotLexer.toState(stack(4, 1)), 14);
        assertEquals(RobotLexer.toState(stack(1, 4, 3)), 341);
        assertEquals(RobotLexer.toState(stack(1, 3, 4)), 431);
        assertEquals(RobotLexer.toState(stack(1, 4, 4)), 441);
    }

    public void testParse() {
        String data = getData("C:\\Users\\mrubino\\hack\\intellibot\\test_resource\\test_feature.robot");

        RobotLexer lexer = new RobotLexer(new RobotKeywordProvider());
        lexer.start(data);
        assertState(lexer, "*** Settings ***", RobotTokenTypes.HEADING, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "# fun things going on here", RobotTokenTypes.COMMENT, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Documentation", RobotTokenTypes.SETTING, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Test the account dashboard", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/db_advertiser_actions.txt", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/db_campaign_actions.txt", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/db_staff_actions.txt", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/ui_login_page.txt", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle/web/ui_manage_accounts_page.txt", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Resource", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "kyle_db_cleanup/kyle_cleanup.txt", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Library", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Selenium2Library", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "timeout=${ENV['selenium']['timeout']}", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "implicit_wait=${ENV['selenium']['implicit_wait']}", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Library", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "db.orm.Orm", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Library", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "OperatingSystem", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Library", RobotTokenTypes.IMPORT, IMPORT);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, IMPORT);
        lexer.advance();
        assertState(lexer, "Collections", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "Force Tags", RobotTokenTypes.SETTING, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Kyle", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Advertiser", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Dashboard", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Component", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "#Other Tag", RobotTokenTypes.COMMENT, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, SETTINGS_HEADING);
        lexer.advance();
        assertState(lexer, "*** Test Cases ***", RobotTokenTypes.HEADING, TEST_CASES_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, TEST_CASES_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, TEST_CASES_HEADING);
        lexer.advance();
        assertState(lexer, "Scenario: An admin can see the conversion trend", RobotTokenTypes.TC_KW_NAME, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Tags]", RobotTokenTypes.BRACKET_SETTING, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Was Flickering", RobotTokenTypes.ARGUMENT, ARG);
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
        assertState(lexer, "MANAGER", RobotTokenTypes.ARGUMENT, ARG);
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
        assertState(lexer, "${ACCOUNT_MANAGER.user.name}", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "${ACCOUNT_MANAGER.user.password}", RobotTokenTypes.ARGUMENT, ARG);
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
        assertState(lexer, "$0.09", RobotTokenTypes.ARGUMENT, ARG);
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
        assertState(lexer, "Close All Browsers", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Clean Database", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "*** Keywords ***", RobotTokenTypes.HEADING, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORDS_HEADING);
        lexer.advance();
        assertState(lexer, "Clean Database", RobotTokenTypes.TC_KW_NAME, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "[Documentation]", RobotTokenTypes.BRACKET_SETTING, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Cleans the database", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Clean", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Kyle", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Clean", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "  ", RobotTokenTypes.WHITESPACE, KEYWORD);
        lexer.advance();
        assertState(lexer, "Ike", RobotTokenTypes.ARGUMENT, ARG);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "    ", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        assertState(lexer, "Close All Browsers", RobotTokenTypes.KEYWORD, KEYWORD);
        lexer.advance();
        assertState(lexer, "\n", RobotTokenTypes.WHITESPACE, KEYWORD_DEFINITION);
        lexer.advance();
        lexer.advance();
    }

    private static void assertState(RobotLexer lexer, String data, RobotElementType token, int state) {
        assertEquals("Data", data, lexer.getBufferSequence().subSequence(lexer.getTokenStart(), lexer.getTokenEnd()));
        assertEquals("State", state, lexer.peekState());
        assertEquals("Token", token, lexer.getTokenType());
    }

    private static void assertStack(Stack<Integer> actual, int... expected) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull(actual);
            assertEquals(actual.size(), expected.length);
            for (int i = 0; i < expected.length; i++) {
                assertEquals((int) actual.get(i), expected[i]);
            }
        }
    }

    private static Stack<Integer> stack(int... values) {
        Stack<Integer> stack = new Stack<Integer>();
        for (int value : values) {
            stack.push(value);
        }
        return stack;
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
