/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class RobotFileTypeHandler extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(RobotFeatureFileType.INSTANCE);
    }
}
