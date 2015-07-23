package com.millennialmedia.intellibot.ide.inspections.readability;

import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.ide.inspections.SimpleRobotInspection;
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
        if (element instanceof KeywordDefinition) {
            return valid(((KeywordDefinition) element).getPresentableText());
        } else {
            return true;
        }
    }

    private boolean valid(String text) {
        Collection<String> gherkin = RobotKeywordProvider.getInstance().getSyntaxOfType(RobotTokenTypes.GHERKIN);
        int firstSpace = text.indexOf(" ");

        String word;
        if (firstSpace < 0) {
            word = text;
        } else {
            word = text.substring(0, firstSpace);
        }
        return !gherkin.contains(word);
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.define.keyword.gherkin.start");
    }
}
