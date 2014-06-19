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
    ImportType getImportType();
}
