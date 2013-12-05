package com.millennialmedia.intellibot.psi;


import com.intellij.testFramework.ParsingTestCase;


public class RobotParserTest extends ParsingTestCase {
    public RobotParserTest() {
        super("", "robot", new RobotParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    public void testJunk() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
//        return "/testData";
        return "C:\\Users\\mrubino\\hack\\intellibot\\testData";
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