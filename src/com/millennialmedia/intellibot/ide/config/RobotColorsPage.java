package com.millennialmedia.intellibot.ide.config;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.RobotFeatureFileType;
import com.millennialmedia.intellibot.psi.RobotHighlighter;
import com.millennialmedia.intellibot.psi.RobotKeywordProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * @author mrubino
 */
public class RobotColorsPage implements ColorSettingsPage {

    private static final String DEMO_TEXT = "invalid\n" +
            "*** Settings ***\n" +
            "Documentation     This is some demo text\n" +
            "Library           CalculatorLibrary\n" +
            "\n" +
            "*** Variables ***\n" +
            "${var1}  12345\n" +
            "${var2}  another variable\n" +
            "\n" +
            "*** Test Cases ***\n" +
            "Addition\n" +
            "  [Tags]  Calculator\n" +
            "    Given calculator has been cleared\n" +
            "    When user types \"1 + 1\"\n" +
            "    And user pushes equals\n" +
            "    Then result is \"2\"\n" +
            "\n" +
            "#Subtraction\n" +
            "#  [Tags]  Calculator\n" +
            "#    TODO: implement me\n" +
            "\n" +
            "*** Keywords ***\n" +
            "Calculator has been cleared\n" +
            "    Push button    C\n" +
            "\n" +
            "User types \"${expression}\"\n" +
            "    Push buttons    ${expression}\n" +
            "\n" +
            "User pushes equals\n" +
            "    Push button    =\n" +
            "\n" +
            "Result is \"${result}\"\n" +
            "    Result should be    ${result}";

    private static final String NAME = "Robot";
    private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];

    private static final AttributesDescriptor[] ATTRIBUTES = new AttributesDescriptor[]{
            new AttributesDescriptor(RobotBundle.message("color.settings.heading"), RobotHighlighter.HEADING),
            new AttributesDescriptor(RobotBundle.message("color.settings.comment"), RobotHighlighter.COMMENT),
            new AttributesDescriptor(RobotBundle.message("color.settings.argument"), RobotHighlighter.ARGUMENT),
            new AttributesDescriptor(RobotBundle.message("color.settings.error"), RobotHighlighter.ERROR),
            new AttributesDescriptor(RobotBundle.message("color.settings.gherkin"), RobotHighlighter.GHERKIN),
            new AttributesDescriptor(RobotBundle.message("color.settings.variable"), RobotHighlighter.VARIABLE),
            new AttributesDescriptor(RobotBundle.message("color.settings.variableDefinition"), RobotHighlighter.VARIABLE_DEFINITION),
            new AttributesDescriptor(RobotBundle.message("color.settings.keyword"), RobotHighlighter.KEYWORD),
            new AttributesDescriptor(RobotBundle.message("color.settings.keywordDefinition"), RobotHighlighter.KEYWORD_DEFINITION),
            new AttributesDescriptor(RobotBundle.message("color.settings.bracketSetting"), RobotHighlighter.BRACKET_SETTING),
            new AttributesDescriptor(RobotBundle.message("color.settings.setting"), RobotHighlighter.SETTING),
            new AttributesDescriptor(RobotBundle.message("color.settings.import"), RobotHighlighter.IMPORT)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return RobotFeatureFileType.getInstance().getIcon();
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new RobotHighlighter(RobotKeywordProvider.getInstance());
    }

    @NotNull
    @Override
    public String getDemoText() {
        return DEMO_TEXT;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRIBUTES;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return COLORS;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return NAME;
    }
}
