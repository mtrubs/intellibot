package com.millennialmedia.intellibot.psi.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class KeywordParserTest {

    private String namespace;
    private String keyword;
    private String pattern;

    public KeywordParserTest(String namespace, String keyword, String pattern) {
        this.namespace = namespace;
        this.keyword = keyword;
        this.pattern = pattern;
    }

    @Test
    public void testBuildPattern() throws Throwable {
        String actual = PatternBuilder.parseNamespaceKeyword(namespace, keyword);
        assertEquals(pattern, actual);

        assertTrue(Pattern.compile(actual).matcher(keyword).matches());
        String temp = keyword.replace("${variable1}", "junk")
                .replace("${variable2}", "junk2")
                .replace("${date_range}", "Yesterday");
        assertTrue(Pattern.compile(actual).matcher(temp).matches());
        assertTrue(Pattern.compile(actual).matcher(namespace + "." + temp).matches());
    }

    @Parameterized.Parameters
    public static Collection patterns() {
        return Arrays.asList(new Object[][]{
                {"my_file", "This is a test keyword", "(\\Qmy_file.\\E)?\\QThis is a test keyword\\E"},
                {"my_file", "This is a test keyword with some ${crap in it", "(\\Qmy_file.\\E)?\\QThis is a test keyword with some ${crap in it\\E"},
                {"my_file", "This is a ${variable1} keyword", "(\\Qmy_file.\\E)?\\QThis is a \\E.*?\\Q keyword\\E"},
                {"my_file", "Set the date range to \"${date_range}\"", "(\\Qmy_file.\\E)?\\QSet the date range to \"\\E.*?\\Q\"\\E"},
                {"my_file", "This is a keyword with a ${variable1}", "(\\Qmy_file.\\E)?\\QThis is a keyword with a \\E.*?"},
                {"my_file", "${variable1} keyword am I", "(\\Qmy_file.\\E)?.*?\\Q keyword am I\\E"},
                {"my_file", "This is a ${variable1} keyword times ${variable2}", "(\\Qmy_file.\\E)?\\QThis is a \\E.*?\\Q keyword times \\E.*?"}
        });
    }
}
