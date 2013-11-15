/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static com.millennialmedia.intellibot.psi.RobotLexer.*;

/**
 * @author mrubino
 */
public class RobotLexerTest extends TestCase {

    public void testParse() {
        String data = getData("C:\\Users\\mrubino\\hack\\intellibot\\test_resource\\test_feature.robot");

        RobotLexer lexer = new RobotLexer(new RobotKeywordProvider());
        lexer.start(data);
        assertState(lexer, IN_SETTINGS_HEADER, "*** Settings ***", RobotTokenTypes.HEADING);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "# fun things going on here", RobotTokenTypes.COMMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_ARG_SETTING, "Documentation", RobotTokenTypes.SETTING);
        lexer.advance();
        assertState(lexer, IN_ARG_SETTING, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "Test the account dashboard", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Resource", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "kyle/web/db_advertiser_actions.txt", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Resource", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "kyle/web/db_campaign_actions.txt", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Resource", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "kyle/web/db_staff_actions.txt", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Resource", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "kyle/web/ui_login_page.txt", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Resource", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "kyle/web/ui_manage_accounts_page.txt", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Resource", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "kyle_db_cleanup/kyle_cleanup.txt", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Library", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "Selenium2Library  timeout=${ENV['selenium']['timeout']}  implicit_wait=${ENV['selenium']['implicit_wait']}", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Library", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "db.orm.Orm", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Library", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "OperatingSystem", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "Library", RobotTokenTypes.IMPORT);
        lexer.advance();
        assertState(lexer, IN_IMPORT, "  ", RobotTokenTypes.TEXT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "Collections", RobotTokenTypes.ARGUMENT);
        lexer.advance();
        assertState(lexer, IN_SETTINGS_HEADER, "\n", RobotTokenTypes.TEXT);
    }

    private static void assertState(RobotLexer lexer, int state, String data, RobotElementType token) {
        assertEquals("State", state, lexer.getState());
        assertEquals("Data", data, lexer.getBufferSequence().subSequence(lexer.getTokenStart(), lexer.getTokenEnd()));
        assertEquals("Token", token, lexer.getTokenType());
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
