package com.millennialmedia.intellibot.ide;

import com.intellij.openapi.editor.markup.RangeHighlighter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author mrubino
 * @since 2016-06-01
 */
public class RobotHighlightTest extends AbstractRobotIdeTest {

    public void testFindKeywordByDefinition() {
        assertHighlights(
                doTest(getTestFile()),
                "calculator has been cleared",
                "calculator has been cleared",
                "Calculator has been cleared"
        );
    }

    public void testFindKeywordByUsage() {
        assertHighlights(
                doTest(getTestFile()),
                "calculator has been cleared",
                "calculator has been cleared",
                "Calculator has been cleared"
        );
    }

    public void testFindInlineKeywordByDefinition() {
        assertHighlights(
                doTest(getTestFile()),
                "user types \"1 + 1\"",
                "user types \"2 - 1\"",
                "User types \"${expression}\""
        );
    }

    public void testFindInlineKeywordByUsage() {
        assertHighlights(
                doTest(getTestFile()),
                "user types \"1 + 1\"",
                "user types \"2 - 1\"",
                "User types \"${expression}\""
        );
    }

    public void testFindVariableByDefinition() {
        assertHighlights(
                doTest(getTestFile()),
                "${var2}",
                "${var2}"
        );
    }

    public void testFindVariableByUsage() {
        assertHighlights(
                doTest(getTestFile()),
                "${var2}",
                "${var2}"
        );
    }

    public void testFindInlineVariableByDefinition() {
        assertHighlights(
                doTest(getTestFile()),
                "${expression}",
                "${expression}"
        );
    }

    public void testFindInlineVariableByUsage() {
        assertHighlights(
                doTest(getTestFile()),
                "${expression}",
                "${expression}"
        );
    }

    private RangeHighlighter[] doTest(String... files) {
        return getFixture().testHighlightUsages(files);
    }


    /**
     * Checks that the highlights that we get for the test file matches our expectations.  This
     * will sort the given highlights based on appearance in file to ensure consistency.
     *
     * @param highlights the results to check.
     * @param expected   the expectations.
     */
    private void assertHighlights(RangeHighlighter[] highlights, String... expected) {
        assertNotNull(highlights);
        assertEquals(expected.length, highlights.length);
        List<RangeHighlighter> sorted = sortHighlights(highlights);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], getHighlightText(sorted.get(i)));
        }
    }

    private List<RangeHighlighter> sortHighlights(RangeHighlighter[] highlights) {
        List<RangeHighlighter> sorted = new ArrayList<RangeHighlighter>(highlights.length);
        Collections.addAll(sorted, highlights);
        Collections.sort(sorted, new Comparator<RangeHighlighter>() {
            @Override
            public int compare(RangeHighlighter o1, RangeHighlighter o2) {
                return Integer.compare(o1.getStartOffset(), o2.getStartOffset());
            }
        });
        return sorted;
    }

    private String getHighlightText(RangeHighlighter highlight) {
        return getFixture().getFile().getText().substring(highlight.getStartOffset(), highlight.getEndOffset());
    }

    @Override
    protected String getBasePath() {
        return "ide/usages/";
    }
}
