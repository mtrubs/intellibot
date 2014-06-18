package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import com.millennialmedia.intellibot.psi.element.RobotFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 * @since 2014-06-16
 */
public class ResolverUtils {

    private ResolverUtils() {
    }

    @Nullable
    public static PsiElement resolveKeywordFromFile(@Nullable String keywordText, @Nullable PsiFile file) {
        if (keywordText == null) {
            return null;
        } else if (file == null) {
            return null;
        } else if (!(file instanceof RobotFile)) {
            return null;
        }
        RobotFile robotFile = (RobotFile) file;
        for (DefinedKeyword keyword : robotFile.getDefinedKeywords()) {
            if (keyword.matches(keywordText)) {
                return keyword.reference();
            }
        }
        for (KeywordFile imported : robotFile.getImportedFiles()) {
            for (DefinedKeyword keyword : imported.getDefinedKeywords()) {
                if (keyword.matches(keywordText)) {
                    return keyword.reference();
                }
            }
        }
        return null;
    }
}
