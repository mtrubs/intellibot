package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author mrubino
 */
public class BracketSettingImpl extends RobotPsiElementBase implements BracketSetting {

    private static final String ARGUMENTS = "[Arguments]";

    public BracketSettingImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.BRACKET_SETTING);
    }

    @Override
    public boolean isArguments() {
        return ARGUMENTS.equalsIgnoreCase(getTextData());
    }

    @Override
    public boolean hasArgs() {
        Collection<PsiElement> args = findChildrenByType(TokenSet.create(RobotTokenTypes.ARGUMENT));
        return args != null && !args.isEmpty();
    }
}