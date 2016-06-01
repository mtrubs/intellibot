package com.millennialmedia.intellibot.ide;

import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageInfo;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;

import java.util.*;

/**
 * @author mrubino
 * @since 2016-06-01
 */
public class RobotFindUsagesTest extends AbstractRobotIdeTest {

    public void testFindKeywordByDefinition() {
        assertUsages(
                doTest(getTestFile()),
                "calculator has been cleared",
                "calculator has been cleared"
        );
    }

    public void testFindKeywordByUsage() {
        assertUsages(
                doTest(getTestFile()),
                "calculator has been cleared",
                "calculator has been cleared"
        );
    }

    public void testFindInlineKeywordByDefinition() {
        assertUsages(
                doTest(getTestFile()),
                "user types \"1 + 1\"",
                "user types \"2 - 1\""
        );
    }

    public void testFindInlineKeywordByUsage() {
        assertUsages(
                doTest(getTestFile()),
                "user types \"1 + 1\"",
                "user types \"2 - 1\""
        );
    }

    public void testFindVariableByDefinition() {
        assertUsages(
                doTest(getTestFile()),
                "${var2}"
        );
    }

    public void testFindVariableByUsage() {
        assertUsages(
                doTest(getTestFile()),
                "${var2}"
        );
    }

    public void testFindInlineVariableByDefinition() {
        assertUsages(
                doTest(getTestFile()),
                "${expression}"
        );
    }

    public void testFindInlineVariableByUsage() {
        assertUsages(
                doTest(getTestFile()),
                "${expression}"
        );
    }

    private Collection<UsageInfo> doTest(String... files) {
        // TODO: seems bit hacky
        RobotOptionsProvider.getInstance(getFixture().getProject()).setInlineVariableSearch(true);
        return getFixture().testFindUsages(files);
    }

    /**
     * Checks that the usage info that we get for the test file matches our expectations.  This
     * will sort the given usages based on appearance in file to ensure consistency.
     *
     * @param usages   the results to check.
     * @param expected the expectations.
     */
    private void assertUsages(Collection<UsageInfo> usages, String... expected) {
        assertNotNull(usages);
        assertEquals(expected.length, usages.size());
        List<UsageInfo> sorted = sortUsages(usages);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], getUsageText(sorted.get(i)));
        }
    }

    private List<UsageInfo> sortUsages(Collection<UsageInfo> usages) {
        List<UsageInfo> sorted = new ArrayList<UsageInfo>(usages);
        Collections.sort(sorted, new Comparator<UsageInfo>() {
            @Override
            public int compare(UsageInfo o1, UsageInfo o2) {
                PsiElement e1 = o1.getElement();
                PsiElement e2 = o2.getElement();
                assertNotNull(e1);
                assertNotNull(e2);
                return Integer.compare(e1.getTextOffset(), e2.getTextOffset());
            }
        });
        return sorted;
    }

    private String getUsageText(UsageInfo usage) {
        PsiElement element = usage.getElement();
        assertNotNull(element);
        int start = element.getTextOffset();
        return getFixture().getFile().getText().substring(start, start + element.getTextLength());
    }

    @Override
    protected String getBasePath() {
        return "ide/usages/";
    }
}
