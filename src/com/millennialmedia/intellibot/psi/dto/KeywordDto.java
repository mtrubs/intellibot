package com.millennialmedia.intellibot.psi.dto;

import com.millennialmedia.intellibot.psi.element.DefinedKeyword;

/**
 * This acts as a wrapper for python definitions.
 *
 * @author mrubino
 * @since 2014-06-06
 */
public class KeywordDto implements DefinedKeyword {

    private String name;
    private boolean args;

    public KeywordDto(String name, boolean args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public String getKeywordName() {
        return this.name;
    }

    @Override
    public boolean hasArguments() {
        return this.args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeywordDto that = (KeywordDto) o;

        // I am not sure if we care about arguments in terms of uniqueness here
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
