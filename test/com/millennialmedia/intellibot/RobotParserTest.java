package com.millennialmedia.intellibot;


import com.intellij.testFramework.ParsingTestCase;
import com.millennialmedia.intellibot.psi.RobotParserDefinition;


public class RobotParserTest extends ParsingTestCase {
    public RobotParserTest() {
        super("", "robot", new RobotParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "/Users/steveabrams/workspace/github/sabrams/intellibot/test/com/millennialmedia/intellibot/testData";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}