package com.millennialmedia.intellibot.psi.element;

import com.intellij.psi.PsiFile;

import java.util.Collection;

/**
 * @author Stephen Abrams
 */
public interface RobotFile extends PsiFile {

    /**
     * @return locally defined keywords
     */
    Collection<String> getKeywords();

    /**
     * Gets all the imported keyword files that are considered in scope for this file.  This
     * includes python libraries and robot resource files.
     *
     * @return a collection of keyword files that this files knows about.
     */
    Collection<KeywordFile> getImportedFiles();
}
