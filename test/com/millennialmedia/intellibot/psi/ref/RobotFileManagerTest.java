package com.millennialmedia.intellibot.psi.ref;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author mrubino
 * @since 2014-06-18
 */
@RunWith(Parameterized.class)
public class RobotFileManagerTest {

    private String path;
    private String suffix;
    private String[] expected;

    public RobotFileManagerTest(String path, String suffix, String[] expected) {
        this.path = path;
        this.suffix = suffix;
        this.expected = expected;
    }


    @Test
    public void testGetFilename() {
        try {
            Method method = RobotFileManager.class.getDeclaredMethod("getFilename", String.class, String.class);
            method.setAccessible(true);
            String[] actual = (String[]) method.invoke(null, this.path, this.suffix);
            assertNotNull(actual);
            assertEquals(2, actual.length);
            assertEquals(this.expected[0], actual[0]);
            assertEquals(this.expected[1], actual[1]);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Parameterized.Parameters
    public static Collection files() {
        return Arrays.asList(new Object[][]{
                {"google_response_files_utils.robot", "", new String[]{"", "google_response_files_utils.robot"}},
                {"kyle/web/ui_login_page.robot", "", new String[]{"kyle/web/", "ui_login_page.robot"}},
                {"utils", ".py", new String[] {"", "utils.py"}},
                {"utils.py", ".py", new String[] {"", "utils.py"}},
                {"kyle/data/archive/users", ".py", new String[] {"kyle/data/archive/", "users.py"}},
                {"kyle/data/archive/users.py", ".py", new String[] {"kyle/data/archive/", "users.py"}}
        });
    }
}
