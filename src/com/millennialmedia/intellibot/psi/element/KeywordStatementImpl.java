package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.dto.VariableDto;
import com.millennialmedia.intellibot.psi.util.PatternUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrubino
 */
public class KeywordStatementImpl extends RobotPsiElementBase implements KeywordStatement {

    private List<Argument> arguments;
    private DefinedVariable variable;
    private KeywordInvokable invokable;

    public KeywordStatementImpl(@NotNull ASTNode node) {
        super(node, RobotTokenTypes.KEYWORD_STATEMENT);
    }

    @Nullable
    @Override
    public KeywordInvokable getInvokable() {
        KeywordInvokable result = this.invokable;
        if (result == null) {
            result = collectInvokable();
            this.invokable = result;
        }
        return result;
    }

    @Nullable
    private KeywordInvokable collectInvokable() {
        for (PsiElement child : getChildren()) {
            if (child instanceof KeywordInvokable) {
                return (KeywordInvokable) child;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public List<Argument> getArguments() {
        List<Argument> results = this.arguments;
        if (results == null) {
            results = collectArguments();
            this.arguments = results;
        }
        return results;
    }

    private List<Argument> collectArguments() {
        List<Argument> results = new ArrayList<Argument>();
        for (PsiElement element : getChildren()) {
            if (element instanceof Argument) {
                results.add((Argument) element);
            }
        }
        return results;
    }

    @Nullable
    @Override
    public DefinedVariable getGlobalVariable() {
        DefinedVariable result = this.variable;
        if (result == null) {
            result = collectGlobalVariable();
            this.variable = result;
        }
        return result;
    }

    @Nullable
    private DefinedVariable collectGlobalVariable() {
        KeywordInvokable invokeable = getInvokable();
        if (invokeable != null) {
            String text = invokeable.getPresentableText();
            if (PatternUtil.isVariableSettingKeyword(text)) {
                List<Argument> arguments = getArguments();
                if (arguments.size() > 0) {
                    Argument variable = arguments.get(0);
                    return new VariableDto(variable, variable.getPresentableText());
                }
            }
        }
        return null;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.arguments = null;
        this.invokable = null;
        this.variable = null;
    }
}
