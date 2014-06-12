package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.impl.source.DummyHolderElement;
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
 * @since 2014-06-08
 */
@RunWith(Parameterized.class)
public class KeywordDefinitionTest {

    private String namespace;
    private String keyword;
    private String pattern;

    public KeywordDefinitionTest(String namespace, String keyword, String pattern) {
        this.namespace = namespace;
        this.keyword = keyword;
        this.pattern = pattern;
    }

    @Test
    public void testBuildPattern() {
        try {
            Method method = KeywordDefinitionImpl.class.getDeclaredMethod("buildPattern", String.class, String.class);
            method.setAccessible(true);
            String actual = (String) method.invoke(new KeywordDefinitionImpl(new DummyHolderElement("dummy")), this.namespace, this.keyword);
            assertEquals(this.pattern, actual);

            assertTrue(Pattern.compile(actual).matcher(this.keyword).matches());
            String temp = this.keyword.replace("${variable1}", "junk")
                    .replace("${variable2}", "junk2")
                    .replace("${date_range}", "Yesterday");
            assertTrue(Pattern.compile(actual).matcher(temp).matches());
            assertTrue(Pattern.compile(actual).matcher(this.namespace + "." + temp).matches());
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
