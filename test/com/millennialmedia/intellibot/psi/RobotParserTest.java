package com.millennialmedia.intellibot.psi;

import com.intellij.testFramework.ParsingTestCase;
import com.millennialmedia.intellibot.ResourceLoader;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
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
    public void testVariables() {
        doTest(true);
    }

    @Test
    public void testTrimmedVariables() {
        doTest(true);
    }

    @Test
    public void testEmptyHeaders() {
        doTest(true);
    }

    @Test
    public void testDemo() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return ResourceLoader.getResourcePath("samples");
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