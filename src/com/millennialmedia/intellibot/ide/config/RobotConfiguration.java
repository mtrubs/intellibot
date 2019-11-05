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
    private JLabel transitiveImports;
    private JCheckBox allowGlobalVariables;
    private JCheckBox capitalizeKeywords;
    private JCheckBox inlineVariableSearch;
    private JCheckBox stripVariableInLibraryPath;
    private JCheckBox searchChildKeywords;
    private JCheckBox loadProjectDefaultVariable;
    private JSpinner maxTransitiveDepth;

    public RobotConfiguration(@NotNull RobotOptionsProvider provider) {
        this.provider = provider;
        maxTransitiveDepth.setModel(new SpinnerNumberModel(this.provider.maxTransitiveDepth(), 0, 99, 1));
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
                this.provider.allowGlobalVariables() != this.allowGlobalVariables.isSelected() ||
                this.provider.capitalizeKeywords() != this.capitalizeKeywords.isSelected() ||
                this.provider.inlineVariableSearch() != this.inlineVariableSearch.isSelected() ||
                this.provider.stripVariableInLibraryPath() != this.stripVariableInLibraryPath.isSelected() ||
                this.provider.searchChildKeywords() != this.searchChildKeywords.isSelected() ||
                this.provider.loadProjectDefaultVariable() != this.loadProjectDefaultVariable.isSelected() ||
                this.provider.maxTransitiveDepth() != (int) this.maxTransitiveDepth.getValue();
    }

    @Override
    public void apply() throws ConfigurationException {
        this.provider.setDebug(this.enableDebug.isSelected());
        this.provider.setGlobalVariables(this.allowGlobalVariables.isSelected());
        this.provider.setCapitalizeKeywords(this.capitalizeKeywords.isSelected());
        this.provider.setInlineVariableSearch(this.inlineVariableSearch.isSelected());
        this.provider.setStripVariableInLibraryPath(this.stripVariableInLibraryPath.isSelected());
        this.provider.setSearchChildKeywords(this.searchChildKeywords.isSelected());
        this.provider.setLoadProjectDefaultVariable(this.loadProjectDefaultVariable.isSelected());
        try {
            this.maxTransitiveDepth.commitEdit();
        } catch (Exception ignore) {}
        this.provider.setMaxTransitiveDepth((int)this.maxTransitiveDepth.getValue());
    }

    @Override
    public void reset() {
        this.enableDebug.setSelected(this.provider.isDebug());
        this.allowGlobalVariables.setSelected(this.provider.allowGlobalVariables());
        this.capitalizeKeywords.setSelected(this.provider.capitalizeKeywords());
        this.inlineVariableSearch.setSelected(this.provider.inlineVariableSearch());
        this.stripVariableInLibraryPath.setSelected(this.provider.stripVariableInLibraryPath());
        this.searchChildKeywords.setSelected(this.provider.searchChildKeywords());
        this.loadProjectDefaultVariable.setSelected(this.provider.loadProjectDefaultVariable());
        this.maxTransitiveDepth.setValue(this.provider.maxTransitiveDepth());
    }

    @Override
    public void disposeUIResources() {
    }
}
