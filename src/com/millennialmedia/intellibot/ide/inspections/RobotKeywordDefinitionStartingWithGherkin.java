package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.RobotKeywordProvider;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.KeywordDefinition;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class RobotKeywordDefinitionStartingWithGherkin extends SimpleRobotInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.message("INSP.NAME.gherkin.format");
    }

    @Override
    public boolean skip(PsiElement element) {
        return element.getNode().getElementType() != RobotTokenTypes.KEYWORD_DEFINITION ||
                element instanceof KeywordDefinition || // this is to skip the whole keyword as opposed to just the text
                valid(element.getText());
    }

    private boolean valid(String text) {
        Collection<String> gherkin = RobotKeywordProvider.getInstance().getSyntaxOfType(RobotTokenTypes.GHERKIN);
        String start = text.substring(0, Math.max(text.indexOf(" "), 0));
        return !gherkin.contains(start);
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.define.keyword.gherkin.start");
    }
}
