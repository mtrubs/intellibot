package com.millennialmedia.intellibot.psi.dto;

import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.element.DefinedVariable;
import com.millennialmedia.intellibot.psi.util.PatternUtil;
import com.millennialmedia.intellibot.psi.util.ReservedVariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * @author mrubino
 * @since 2014-06-18
 */
public class VariableDto implements DefinedVariable {

    private final PsiElement reference;
    private final String name;
    private final ReservedVariableScope scope;
    private Pattern pattern;

    public VariableDto(@NotNull PsiElement reference, @NotNull String name, @Nullable ReservedVariableScope scope) {
        this.reference = reference;
        this.name = name.trim();
        this.scope = scope;
    }

    @Override
    public boolean matches(@Nullable String text) {
        if (text == null) {
            return false;
        }
        Pattern pattern = this.pattern;
        if (pattern == null) {
            pattern = Pattern.compile(PatternUtil.getVariablePattern(this.name), Pattern.CASE_INSENSITIVE);
            this.pattern = pattern;
        }
        return pattern.matcher(text).matches();
    }

    @Override
    public boolean isInScope(@Nullable PsiElement position) {
        return this.scope == null || position == null || this.scope.isInScope(position);
    }

    @Nullable
    @Override
    public PsiElement reference() {
        return this.reference;
    }

    @Nullable
    @Override
    public String getLookup() {
        // TODO: why return this.reference.getText() ?
        // return this.scope == null ? this.reference.getText() : this.name;
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableDto that = (VariableDto) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
