package com.millennialmedia.intellibot.psi.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author mrubino
 * @since 2014-06-19
 */
@RunWith(Parameterized.class)
public class PatternUtilBuildPatternTest {

    private static final String EXPECTED = "[\\$\\@\\%\\&]\\{[ _]*?\\Qv\\E[ _]*?\\Qa\\E[ _]*?\\Qr\\E[ _]*?\\Qi\\E[ _]*?\\Qa\\E[ _]*?\\Qb\\E[ _]*?\\Ql\\E[ _]*?\\Qe\\E[ _]*?((\\..*?)*?(\\[.*?\\])*?)*?\\}(\\[\\d+\\])?";
    private String name;
    private String expected;

    public PatternUtilBuildPatternTest(String name, String expected) {
        this.name = name;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection patterns() {
        return Arrays.asList(new Object[][]{
                {"variable", EXPECTED},
                {"${variable}", EXPECTED},
                {"${var_i able}", EXPECTED},
                {"${variable}=", EXPECTED},
                {"${variable} =", EXPECTED},
                {" ${variable} = ", EXPECTED},
                {"@{variable}", EXPECTED},
                {"@{var_i able}", EXPECTED},
                {"@{variable}=", EXPECTED},
                {"@{variable} =", EXPECTED},
                {" @{variable} = ", EXPECTED},
                {"%{variable}", EXPECTED},
                {"%{var_i able}", EXPECTED},
                {"%{variable}=", EXPECTED},
                {"%{variable} =", EXPECTED},
                {" %{variable} = ", EXPECTED},
                {"&{variable}", EXPECTED},
                {"&{var_i able}", EXPECTED},
                {"&{variable}=", EXPECTED},
                {"&{variable} =", EXPECTED},
                {" &{variable} = ", EXPECTED},
        });
    }

    @Test
    public void testBuildPattern() {
        String actual = PatternUtil.getVariablePattern(this.name);
        assertEquals(this.expected, actual);
        Pattern pattern = Pattern.compile(actual);
        // TODO: asserts that either [@,$,%,&] are interchangeable in terms of variable matching/linking; acceptable?
        assertTrue(pattern.matcher("${variable}").matches());
        assertTrue(pattern.matcher("${var i_able}").matches());
        assertTrue(pattern.matcher("${var  i__able}").matches());
        assertTrue(pattern.matcher("${variable['a']['b']}").matches());
        assertTrue(pattern.matcher("${variable['a'].name}").matches());
        assertTrue(pattern.matcher("${variable.name}").matches());
        assertTrue(pattern.matcher("${variable.name['a']}").matches());

        assertTrue(pattern.matcher("@{variable}").matches());
        assertTrue(pattern.matcher("@{var i_able}").matches());
        assertTrue(pattern.matcher("@{var  i__able}").matches());
        assertTrue(pattern.matcher("@{variable['a']['b']}").matches());
        assertTrue(pattern.matcher("@{variable['a'].name}").matches());
        assertTrue(pattern.matcher("@{variable.name}").matches());
        assertTrue(pattern.matcher("@{variable.name['a']}").matches());

        assertTrue(pattern.matcher("%{variable}").matches());
        assertTrue(pattern.matcher("%{var i_able}").matches());
        assertTrue(pattern.matcher("%{var  i__able}").matches());
        assertTrue(pattern.matcher("%{variable['a']['b']}").matches());
        assertTrue(pattern.matcher("%{variable['a'].name}").matches());
        assertTrue(pattern.matcher("%{variable.name}").matches());
        assertTrue(pattern.matcher("%{variable.name['a']}").matches());

        assertTrue(pattern.matcher("&{variable}").matches());
        assertTrue(pattern.matcher("&{var i_able}").matches());
        assertTrue(pattern.matcher("&{var  i__able}").matches());
        assertTrue(pattern.matcher("&{variable['a']['b']}").matches());
        assertTrue(pattern.matcher("&{variable['a'].name}").matches());
        assertTrue(pattern.matcher("&{variable.name}").matches());
        assertTrue(pattern.matcher("&{variable.name['a']}").matches());
    }
}
