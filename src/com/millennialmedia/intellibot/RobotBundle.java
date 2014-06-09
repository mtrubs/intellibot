package com.millennialmedia.intellibot;

import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

/**
 * @author mrubino
 */
public class RobotBundle {

    // based off python's

    @NonNls
    private static final String BUNDLE = "com.millennialmedia.intellibot.RobotBundle";

    private static Reference<ResourceBundle> instance;

    private RobotBundle() {
    }

    public static String message(@NonNls @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    // Cached loading
    private static ResourceBundle getBundle() {
        ResourceBundle bundle = SoftReference.dereference(instance);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            instance = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }
}
