package com.millennialmedia.intellibot.ide.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author mrubino
 * @since 2014-06-26
 */
public class RobotConfiguration implements SearchableConfigurable, Configurable.NoScroll {

    private RobotOptionsProvider provider;

    private JPanel panel;
    private JCheckBox enableDebug;
    private JCheckBox allowTransitiveImports;
    private JCheckBox allowGlobalVariables;
    private JCheckBox capitalizeKeywords;
    private JCheckBox inlineVariableSearch;

    public RobotConfiguration(@NotNull RobotOptionsProvider provider) {
        this.provider = provider;
    }

    @NotNull
    @Override
    public String getId() {
        return getHelpTopic();
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Robot Options";
    }

    @NotNull
    @Override
    public String getHelpTopic() {
        return "reference.idesettings.robot";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return this.panel;
    }

    @Override
    public boolean isModified() {
        return this.provider.isDebug() != this.enableDebug.isSelected() ||
                this.provider.allowTransitiveImports() != this.allowTransitiveImports.isSelected() ||
                this.provider.allowGlobalVariables() != this.allowGlobalVariables.isSelected() ||
                this.provider.capitalizeKeywords() != this.capitalizeKeywords.isSelected() ||
                this.provider.inlineVariableSearch() != this.inlineVariableSearch.isSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        this.provider.setDebug(this.enableDebug.isSelected());
        this.provider.setTransitiveImports(this.allowTransitiveImports.isSelected());
        this.provider.setGlobalVariables(this.allowGlobalVariables.isSelected());
        this.provider.setCapitalizeKeywords(this.capitalizeKeywords.isSelected());
        this.provider.setInlineVariableSearch(this.inlineVariableSearch.isSelected());
    }

    @Override
    public void reset() {
        this.enableDebug.setSelected(this.provider.isDebug());
        this.allowTransitiveImports.setSelected(this.provider.allowTransitiveImports());
        this.allowGlobalVariables.setSelected(this.provider.allowGlobalVariables());
        this.capitalizeKeywords.setSelected(this.provider.capitalizeKeywords());
        this.inlineVariableSearch.setSelected(this.provider.inlineVariableSearch());
    }

    @Override
    public void disposeUIResources() {
    }
}
