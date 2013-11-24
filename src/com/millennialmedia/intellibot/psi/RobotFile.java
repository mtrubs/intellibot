package com.millennialmedia.intellibot.psi;

import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * @author Stephen Abrams
 */
public interface RobotFile extends PsiFile {
    List<String> getKeywords();
    //todo
}
