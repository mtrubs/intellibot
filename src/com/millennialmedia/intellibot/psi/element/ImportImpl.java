package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class ImportImpl extends RobotPsiElementBase implements Import {

    public ImportImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.IMPORT);
    }

    @Override
    public String getPresentableText() {
        return getTextData();
    }

    public boolean isResource() {
        // TODO: better OO
        String text = getTextData();
        return text != null && text.equals("Resource");
    }

    public boolean isVariables() {
        // TODO: better OO
        String text = getTextData();
        return text != null && text.equals("Variables");
    }

    public boolean isLibrary() {
        String text = getTextData();
        return text != null && text.equals("Library");
    }
}