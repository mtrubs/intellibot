package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import com.millennialmedia.intellibot.psi.element.KeywordInvokable;
import com.millennialmedia.intellibot.psi.element.RobotFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class RobotKeywordNotFound extends SimpleRobotInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.message("INSP.NAME.keyword.undefined");
    }

    @Override
    public boolean skip(PsiElement element) {
        return element.getNode().getElementType() != RobotTokenTypes.KEYWORD ||
                element instanceof KeywordInvokable || // this is to skip the whole keyword as opposed to just the text
                !(element.getContainingFile() instanceof RobotFile) ||
                valid(element.getText(), (RobotFile) element.getContainingFile());
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.keyword.undefined");
    }

    private boolean valid(String text, RobotFile file) {
        Collection<String> keywords = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        for (DefinedKeyword keyword : file.getKeywords()) {
            keywords.add(keyword.getKeywordName());
        }
        for (KeywordFile imported : file.getImportedFiles()) {
            for (DefinedKeyword keyword : imported.getKeywords()) {
                keywords.add(keyword.getKeywordName());
            }
        }

        return keywords.contains(text);
    }
}
