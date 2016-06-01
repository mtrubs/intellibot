package com.millennialmedia.intellibot.ide;

/**
 * @author mrubino
 * @since 2016-06-01
 */
public class RobotFoldingTest extends AbstractRobotIdeTest {

    public void testKeywordFolding() {
        doTest();
    }

    private void doTest() {
        getFixture().testFoldingWithCollapseStatus(getTestFile());
    }

    @Override
    protected String getBasePath() {
        return "ide/folding/";
    }
}
