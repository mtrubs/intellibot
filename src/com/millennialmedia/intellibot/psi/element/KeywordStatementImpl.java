package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author mrubino
 */
public class KeywordStatementImpl extends RobotPsiElementBase implements KeywordStatement {

    private Collection<Argument> arguments;

    public KeywordStatementImpl(@NotNull ASTNode node) {
        super(node, RobotTokenTypes.KEYWORD_STATEMENT);
    }

    @NotNull
    @Override
    public Collection<Argument> getArguments() {
        Collection<Argument> results = this.arguments;
        if (results == null) {
            results = collectArguments();
            this.arguments = results;
        }
        return results;
    }

    private Collection<Argument> collectArguments() {
        Collection<Argument> results = new ArrayList<Argument>();
        for (PsiElement element : getChildren()) {
            if (element instanceof Argument) {
                results.add((Argument) element);
            }
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.arguments = null;
    }
}
