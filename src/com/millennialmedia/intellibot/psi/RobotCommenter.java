/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 */
public class RobotCommenter implements Commenter {

    private static final String LINE_COMMENT_PREFIX = "#";

    @Nullable
    public String getLineCommentPrefix() {
        return LINE_COMMENT_PREFIX;
    }

    @Nullable
    public String getBlockCommentPrefix() {
        return null;
    }

    @Nullable
    public String getBlockCommentSuffix() {
        return null;
    }

    @Nullable
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
