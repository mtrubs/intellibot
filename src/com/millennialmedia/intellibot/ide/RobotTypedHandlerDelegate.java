package com.millennialmedia.intellibot.ide;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.psi.RobotLanguage;
import org.jetbrains.annotations.NotNull;

import static com.millennialmedia.intellibot.ide.RobotCompletionContributor.CELL_SEPRATOR_SPACE;

/**
 * Note:
 * !!! press \t, \n neither trigger TypedHandlerDelegate, nor trigger TypedActionHandler
 * so here use 2 space to trigger auto filling of 4 space
 */
public class RobotTypedHandlerDelegate extends TypedHandlerDelegate {

    @NotNull
    @Override
    public TypedHandlerDelegate.Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (c == ' ') {
            if (file.getLanguage() != RobotLanguage.INSTANCE || ! RobotOptionsProvider.getInstance(project).expandSuperSpaces())
                return Result.CONTINUE;
            Document document = editor.getDocument();
            CharSequence chars = document.getCharsSequence();
            CaretModel caretModel = editor.getCaretModel();
            int offset = caretModel.getOffset();
            int textLength = document.getTextLength();
            int spaceBefore = 0;
            for (int i = offset - 1; i >= 0 && chars.charAt(i) == ' '; i--) {
                if (++spaceBefore >= CELL_SEPRATOR_SPACE)
                    break;
            }
            if (spaceBefore >= CELL_SEPRATOR_SPACE || spaceBefore < 2)
                return Result.CONTINUE;
            int spaceAfter = 0;
            for (int i = offset; i < textLength && chars.charAt(i) == ' '; i++) {
                if (++spaceAfter >= (CELL_SEPRATOR_SPACE - 2))
                    break;
            }
            int lenToAdd = CELL_SEPRATOR_SPACE - (spaceBefore + spaceAfter);
            if (lenToAdd <= 0)
                return Result.CONTINUE;
            String toAdd = new String(new char[lenToAdd]).replace("\0", " ");
            Runnable runnable = () -> document.insertString(offset, toAdd);
            WriteCommandAction.runWriteCommandAction(project, runnable);
            caretModel.moveToOffset(offset + lenToAdd + spaceAfter);
            return Result.STOP;
        }
        return Result.CONTINUE;
    }
}