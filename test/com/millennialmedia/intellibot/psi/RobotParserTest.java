package com.millennialmedia.intellibot.psi;


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
        return "/testData";
    }

    @Override
    protected boolean skipSpaces() {
        return true;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}