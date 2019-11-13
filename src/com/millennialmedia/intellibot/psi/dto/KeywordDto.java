package com.millennialmedia.intellibot.psi.dto;

import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.util.PatternBuilder;
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



    private final PsiElement reference;
    private final String name;
    private final boolean args;
    private final Pattern namePattern;
    private final String namespace;

    public KeywordDto(@NotNull PsiElement reference, @NotNull String namespace, @NotNull String name, boolean args) {
        this.reference = reference;
        this.namespace = namespace;
        this.name = PatternUtil.functionToKeyword(name).trim();
        this.namePattern = Pattern.compile(PatternBuilder.parseNamespaceKeyword(namespace, this.name), Pattern.CASE_INSENSITIVE);
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
    public boolean matches(String text) {
        if (text == null)
            return false;
        text = text.trim();
        int p = text.lastIndexOf('.');
        if (p >= 0) {
            String lib = text.substring(0, p);
            String kw = PatternUtil.functionToKeyword(text.substring(p+1));
            text = lib + "." + kw;
        } else {
            text = PatternUtil.functionToKeyword(text);
        }
        return this.namePattern.matcher(text).matches();
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

    @Override
    public String getNamespace() {
        return this.namespace;
    }
}
