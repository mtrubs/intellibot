/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * @author mrubino
 */
public class RobotBundle {

    @NonNls
    private static final String BUNDLE = "com.millennialmedia.intellibot.RobotBundle";

    private static final Reference<ResourceBundle> INSTANCE =
            new SoftReference<ResourceBundle>(ResourceBundle.getBundle(BUNDLE));

    private RobotBundle() {
    }

    public static String message(@NonNls @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return CommonBundle.message(INSTANCE.get(), key, params);
    }
}
