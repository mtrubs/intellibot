package com.millennialmedia.intellibot.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.millennialmedia.intellibot.psi.RobotFile;
import com.millennialmedia.intellibot.psi.RobotFileType;
import com.millennialmedia.intellibot.psi.RobotLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class RobotFileImpl extends PsiFileBase implements RobotFile {

    public RobotFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, RobotLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return RobotFileType.INSTANCE;
    }
}
