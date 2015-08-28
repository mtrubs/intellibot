package com.millennialmedia.intellibot.psi.dto;

import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.util.PatternUtil;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * This acts as a wrapper for python definitions.
 *
 * @author mrubino
 * @since 2014-06-06
 */
public class KeywordDto implements DefinedKeyword {

    private static final String DOT = ".";

    private PsiElement reference;
    private String name;
    private boolean args;
    private Pattern namePattern;

    public KeywordDto(PsiElement reference, @NotNull String namespace, @NotNull String name, boolean args) {
        this.reference = reference;
        this.name = PatternUtil.functionToKeyword(name).trim();
        this.namePattern = Pattern.compile(buildPattern(namespace, this.name), Pattern.CASE_INSENSITIVE);
        this.args = args;
    }

    private String buildPattern(@NotNull String namespace, @NotNull String name) {
        if (namespace.length() > 0) {
            namespace = "(" + Pattern.quote(namespace + DOT) + ")?";
        }
        return namespace + Pattern.quote(name);
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
    public boolean matches(String text) {
        return text != null &&
                this.namePattern.matcher(PatternUtil.functionToKeyword(text).trim()).matches();
    }

    @Override
    public PsiElement reference() {
        return this.reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeywordDto that = (KeywordDto) o;

        // I am not sure if we care about arguments in terms of uniqueness here
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
