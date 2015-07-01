package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.DefinedVariable;
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
        boolean includeTransitive = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
        for (KeywordFile imported : robotFile.getImportedFiles(includeTransitive)) {
            for (DefinedKeyword keyword : imported.getDefinedKeywords()) {
                if (keyword.matches(keywordText)) {
                    return keyword.reference();
                }
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement resolveVariableFromFile(@Nullable String variableText, @Nullable PsiFile file) {
        if (variableText == null) {
            return null;
        } else if (file == null) {
            return null;
        } else if (!(file instanceof RobotFile)) {
            return null;
        }
        RobotFile robotFile = (RobotFile) file;
        for (DefinedVariable variable : robotFile.getDefinedVariables()) {
            if (variable.matches(variableText)) {
                return variable.reference();
            }
        }
        boolean includeTransitive = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
        for (KeywordFile imported : robotFile.getImportedFiles(includeTransitive)) {
            for (DefinedVariable variable : imported.getDefinedVariables()) {
                if (variable.matches(variableText)) {
                    return variable.reference();
                }
            }
        }
        // TODO: __init__ files...
        // TODO: global variables: ~/.robot-env/lib/python2.7/site-packages/robot/variables/__init__.py
        return null;
    }
}
