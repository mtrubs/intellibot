package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.dto.VariableDto;
import com.millennialmedia.intellibot.psi.util.PatternUtil;
import com.millennialmedia.intellibot.psi.util.PerformanceCollector;
import com.millennialmedia.intellibot.psi.util.PerformanceEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrubino
 */
public class KeywordStatementImpl extends RobotPsiElementBase implements KeywordStatement, PerformanceEntity {

    private List<Argument> arguments;
    private DefinedVariable variable;
    private KeywordInvokable invokable;

    public KeywordStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public KeywordInvokable getInvokable() {
        KeywordInvokable result = this.invokable;
        if (result == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "invokable");
            result = collectInvokable();
            this.invokable = result;
            debug.complete();
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
            PerformanceCollector debug = new PerformanceCollector(this, "arguments");
            results = collectArguments();
            this.arguments = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
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
            PerformanceCollector debug = new PerformanceCollector(this, "global variable");
            result = collectGlobalVariable();
            this.variable = result;
            debug.complete();
        }
        return result;
    }

    @Nullable
    private DefinedVariable collectGlobalVariable() {
        KeywordInvokable invokable = getInvokable();
        if (invokable != null) {
            String text = invokable.getPresentableText();
            if (PatternUtil.isVariableSettingKeyword(text)) {
                List<Argument> arguments = getArguments();
                if (arguments.size() > 0) {
                    Argument variable = arguments.get(0);
                    // already formatted ${X}
                    return new VariableDto(variable, variable.getPresentableText(), null);
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

    @NotNull
    @Override
    public String getDebugText() {
        KeywordInvokable invokable = getInvokable();
        String text = invokable == null ? null : getInvokable().getPresentableText();
        return text == null ? "EMPTY" : text;
    }
}
