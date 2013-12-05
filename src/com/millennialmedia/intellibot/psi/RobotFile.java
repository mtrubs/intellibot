package com.millennialmedia.intellibot.psi;

import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * @author Stephen Abrams
 */
public interface RobotFile extends PsiFile {
    /**
     *
     * @return locally defined keywords
     */
    List<String> getKeywords();

    /**
     *
     * @return list of imports, in the order they are declared
     */
    List<RobotFile> getImportedRobotFiles();
}
