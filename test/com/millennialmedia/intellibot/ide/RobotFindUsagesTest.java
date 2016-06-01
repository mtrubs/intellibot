package com.millennialmedia.intellibot.ide;

import com.intellij.usageView.UsageInfo;

import java.util.Collection;

/**
 * @author mrubino
 * @since 2016-06-01
 */
public class RobotFindUsagesTest extends AbstractRobotIdeTest {

    public void testDemo() {
        Collection<UsageInfo> usageInfo = doTest(getTestFile());
        assertNotNull(usageInfo);
    }

    private Collection<UsageInfo> doTest(String... files) {
        return getFixture().testFindUsages(files);
    }

    @Override
    protected String getBasePath() {
        return "ide/usages/";
    }
}
