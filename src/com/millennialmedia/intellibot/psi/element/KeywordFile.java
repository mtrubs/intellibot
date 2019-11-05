package com.millennialmedia.intellibot.psi.element;

import com.millennialmedia.intellibot.psi.dto.ImportType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mrubino
 * Date: 1/28/14
 * Time: 8:01 PM
 */
public interface KeywordFile {

    @NotNull
    Collection<DefinedKeyword> getDefinedKeywords();
    
    @NotNull
    Collection<DefinedVariable> getDefinedVariables();

    @NotNull
    Collection<DefinedVariable> getOwnDefinedVariables();
    
    @NotNull
    ImportType getImportType();

    /*
     * maxTransitiveDepth == 0, no imported files
     * maxTransitiveDepth == 1, only level 1 imported files (as includeTransitive = false before)
     * maxTransitiveDepth ==-1, get the value from configuration options
     */
    @NotNull
    Collection<KeywordFile> getImportedFiles(int maxTransitiveDepth);
}
