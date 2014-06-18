package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class VariableDefinitionImpl extends RobotPsiElementBase implements VariableDefinition {

    public VariableDefinitionImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.VARIABLE_DEFINITION);
    }
    
    private String getPresentableText() {
        return getTextData();
    }

    @Override
    public boolean matches(String text) {
        if (text == null) {
            return false;
        }
        String myText = getPresentableText(); 
        if (myText == null) {
            return false;
        } else {
            myText = myText.trim();
            if (myText.endsWith("=")) {
                myText = myText.substring(0, myText.length() - 1);
            }
            myText = myText.trim();
            return myText.equalsIgnoreCase(text);
        }
    }

    @Override
    public PsiElement reference() {
        return this;
    }
}
