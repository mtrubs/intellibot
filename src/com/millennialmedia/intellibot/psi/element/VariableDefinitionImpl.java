package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.util.PatternUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * @author mrubino
 */
public class VariableDefinitionImpl extends RobotPsiElementBase implements VariableDefinition, DefinedVariable {

    private Pattern pattern;

    public VariableDefinitionImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.pattern = null;
    }

    @Override
    public boolean matches(String text) {
        if (text == null) {
            return false;
        }
        String myText = getPresentableText();
        Pattern pattern = this.pattern;
        if (pattern == null) {
            pattern = Pattern.compile(PatternUtil.getVariablePattern(myText), Pattern.CASE_INSENSITIVE);
            this.pattern = pattern;
        }
        return pattern.matcher(text).matches();
    }

    @Nullable
    @Override
    public PsiElement reference() {
        return this;
    }

    @Override
    public boolean isNested() {
        String text = getPresentableText();
        return StringUtil.getOccurrenceCount(text, "}") > 1 &&
                (StringUtil.getOccurrenceCount(text, "${") + StringUtil.getOccurrenceCount(text, "@{") + StringUtil.getOccurrenceCount(text, "%{") > 1);
    }
}
