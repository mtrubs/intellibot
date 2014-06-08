package com.millennialmedia.intellibot.psi.element;

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
    Collection<DefinedKeyword> getKeywords();
}
