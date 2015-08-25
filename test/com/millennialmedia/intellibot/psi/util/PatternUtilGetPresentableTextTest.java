package com.millennialmedia.intellibot.psi.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author mrubino
 * @since 2014-06-19
 */
@RunWith(Parameterized.class)
public class PatternUtilGetPresentableTextTest {

    private String text;
    private String expected;

    public PatternUtilGetPresentableTextTest(String text, String expected) {
        this.text = text;
        this.expected = expected;
    }

    @Test
    public void testPresentableTest() {
        String actual = PatternUtil.getPresentableText(this.text);
        assertEquals(this.expected, actual);
    }

    @Parameterized.Parameters
    public static Collection patterns() {
        return Arrays.asList(new Object[][]{
                {null, ""},
                {"", ""},
                {" ", ""},
                {"  ", ""},
                {"    ", ""},
                {"\t", ""},
                {"\n", ""},
                {"word", "word"},
                {"this is a sentence", "this is a sentence"},
                {"this is a sentence  with an argument", "this is a sentence"},
                {"this is a sentence    with an argument", "this is a sentence"},
                {"this is a sentence\twith an argument", "this is a sentence"},
                {"this is a sentence\nwith another sentence", "this is a sentence"},
                {" this is a sentence \nwith another sentence", "this is a sentence"},
        });
    }
}
