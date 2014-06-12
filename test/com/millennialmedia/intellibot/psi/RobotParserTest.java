package com.millennialmedia.intellibot.psi;

import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;

public class RobotParserTest extends ParsingTestCase {

    public RobotParserTest() {
        super("", "robot", new RobotParserDefinition());
    }

    @Test
    public void testParsingTestData() {
        doTest(true);
    }

    @Test
    public void testJunk() {
        doTest(true);
    }

    @Test
    public void testEmptyHeaders() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        // TODO: weak
//        return "/samples";
        return "/Users/mrubino/hack/intellibot/testData/samples";
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