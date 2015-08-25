package com.millennialmedia.intellibot.ide.inspections.readability;

import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.ide.inspections.SimpleInspection;
import com.millennialmedia.intellibot.ide.inspections.SimpleRobotInspection;
import com.millennialmedia.intellibot.psi.RobotKeywordProvider;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class RobotGherkinInspection extends SimpleRobotInspection implements SimpleInspection {

    private static final Collection<String> NORMAL;
    private static final Collection<String> UPPER;

    static {
        NORMAL = new HashSet<String>();
        UPPER = new HashSet<String>();
        for (String gherkin : RobotKeywordProvider.getInstance().getSyntaxOfType(RobotTokenTypes.GHERKIN)) {
            NORMAL.add(gherkin);
            UPPER.add(gherkin.toUpperCase());
        }
    }

    public boolean allowUppercase = false;

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.message("INSP.NAME.gherkin.format");
    }

    @Override
    public JComponent createOptionsPanel() {
        MultipleCheckboxOptionsPanel panel = new MultipleCheckboxOptionsPanel(this);
        panel.addCheckbox(RobotBundle.message("INSP.OPT.gherkin.format.upper"), "allowUppercase");
        return panel;
    }

    @Override
    public boolean skip(PsiElement element) {
        // this should be getPresentableText if we ever formalize the Gherkin type
        return element.getNode().getElementType() != RobotTokenTypes.GHERKIN ||
                valid(element.getText());
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.gherkin.format");
    }

    private boolean valid(String text) {
        return NORMAL.contains(text) || (this.allowUppercase && UPPER.contains(text));
    }

    @NotNull
    @Override
    protected String getGroupNameKey() {
        return "INSP.GROUP.readability";
    }
}
