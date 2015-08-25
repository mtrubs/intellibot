package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class ImportImpl extends RobotPsiElementBase implements Import {

    public ImportImpl(@NotNull final ASTNode node) {
        super(node);
    }

    public boolean isResource() {
        // TODO: better OO
        String text = getPresentableText();
        return text.equals("Resource");
    }

    public boolean isVariables() {
        // TODO: better OO
        String text = getPresentableText();
        return text.equals("Variables");
    }

    public boolean isLibrary() {
        // TODO: better OO
        String text = getPresentableText();
        return text.equals("Library");
    }
}