package com.millennialmedia.intellibot.psi.manip;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import com.millennialmedia.intellibot.psi.Import;

/**
 * @author Scott Albertine
 */
public class ImportManipulator extends AbstractElementManipulator<Import> {

    @Override
    public Import handleContentChange(Import element, TextRange range, String newContent) throws IncorrectOperationException {
        // TODO: we seem to need this but it is not really used at this time.  it prevents NPEs for the jump to source.
        // TODO: we likely need it to do intelligent refactoring.
        return null;
    }
}
