package com.millennialmedia.intellibot.ide.inspections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author mrubino
 * @since 2014-06-07
 */
@RunWith(Parameterized.class)
public class RobotKeywordNotFoundTest {

    private String keyword;
    private String pattern;

    public RobotKeywordNotFoundTest(String keyword, String pattern) {
        this.keyword = keyword;
        this.pattern = pattern;
    }

    @Test
    public void testBuildPattern() {
        try {
            Method method = RobotKeywordNotFound.class.getDeclaredMethod("buildPattern", String.class);
            method.setAccessible(true);
            String actual = (String) method.invoke(new RobotKeywordNotFound(), this.keyword);
            assertEquals(this.pattern, actual);

            assertTrue(Pattern.compile(actual).matcher(this.keyword).matches());
            String temp = this.keyword.replace("${variable1}", "junk").replace("${variable2}", "junk2");
            assertTrue(Pattern.compile(actual).matcher(temp).matches());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Parameterized.Parameters
    public static Collection patterns() {
        return Arrays.asList(new Object[][]{
                {"", ""},
                {"This is a test keyword", "\\QThis is a test keyword\\E"},
                {"This is a ${variable1} keyword", "\\QThis is a \\E.*?\\Q keyword\\E"},
                {"This is a keyword with a ${variable1}", "\\QThis is a keyword with a \\E.*?"},
                {"${variable1} keyword am I", ".*?\\Q keyword am I\\E"},
                {"This is a ${variable1} keyword times ${variable2}", "\\QThis is a \\E.*?\\Q keyword times \\E.*?"}
        });
    }
}
