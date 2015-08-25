package com.millennialmedia.intellibot.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2014-02-16
 */
public class RecommendationWord {

    private final String presentation;
    private final String lookup;

    public RecommendationWord(@NotNull String presentation, @NotNull String lookup) {
        this.presentation = presentation;
        this.lookup = lookup;
    }

    @NotNull
    public String getPresentation() {
        return this.presentation;
    }

    @NotNull
    public String getLookup() {
        return this.lookup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecommendationWord that = (RecommendationWord) o;

        if (!this.lookup.equals(that.lookup)) return false;
        //noinspection RedundantIfStatement
        if (!this.presentation.equals(that.presentation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.presentation.hashCode();
        result = 31 * result + this.lookup.hashCode();
        return result;
    }
}
