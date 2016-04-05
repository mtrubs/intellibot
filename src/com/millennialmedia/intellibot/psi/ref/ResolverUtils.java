package com.millennialmedia.intellibot.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.psi.element.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

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
        return null;
    }

    @Nullable
    public static PsiElement resolveVariableFromStatement(@Nullable String variableText, @Nullable PsiElement parent,
                                                          boolean allowGlobalVariables) {
        if (variableText == null) {
            return null;
        } else if (parent == null) {
            return null;
        }
        if (parent instanceof Argument || parent instanceof KeywordInvokable) {
            parent = parent.getParent();
        }
        PsiElement containingStatement = parent.getParent();
        if (containingStatement instanceof VariableDefinition) {
            parent = containingStatement;
            containingStatement = containingStatement.getParent();
        }
        if (containingStatement instanceof KeywordDefinition) {
            // we want to go backwards to get the latest setter
            PsiElement[] children = containingStatement.getChildren();
            boolean seenParent = false;
            for (int i = children.length - 1; i >= 0; i--) {
                PsiElement child = children[i];
                // skip everything until we go past ourselves
                if (child == parent) {
                    seenParent = true;
                    continue;
                }
                if (!seenParent) {
                    continue;
                }
                // now start checking for definitions
                if (child instanceof DefinedVariable) {
                    // ${x}  some keyword results
                    if (((DefinedVariable) child).matches(variableText)) {
                        return child;
                    }
                } else if (child instanceof KeywordStatement) {
                    PsiElement reference = allowGlobalVariables ? walkKeyword((KeywordStatement) child, variableText) : null;
                    if (reference != null) {
                        return reference;
                    }
                }
            }
            for (DefinedVariable variable : ((KeywordDefinition) containingStatement).getDeclaredVariables()) {
                if (variable.matches(variableText)) {
                    return variable.reference();
                }
            }
        }
        return null;
    }

    /**
     * Walks the keyword tree looking for global variable setting keywords.
     * This only includes variables that are set in this manner as everything else
     * is out of scope.
     *
     * @param statement            the keyword statement to find a variable in.
     * @param text                 the variable text we are looking for.
     * @return the matching definition if it exists; null otherwise.
     */
    @Nullable
    private static PsiElement walkKeyword(@Nullable KeywordStatement statement, String text) {
        if (statement == null) {
            return null;
        }
        // set test variable  ${x}  ${y}
        DefinedVariable variable = statement.getGlobalVariable();
        if (variable != null && variable.matches(text)) {
            return variable.reference();
        } else {
            KeywordInvokable invokable = statement.getInvokable();
            if (invokable != null) {
                PsiReference reference = invokable.getReference();
                if (reference != null) {
                    PsiElement resolved = reference.resolve();
                    if (resolved instanceof KeywordDefinition) {
                        List<KeywordInvokable> keywords = ((KeywordDefinition) resolved).getInvokedKeywords();
                        Collections.reverse(keywords);
                        for (KeywordInvokable invoked : keywords) {
                            PsiElement parent = invoked.getParent();
                            if (parent instanceof KeywordStatement) {
                                PsiElement result = walkKeyword((KeywordStatement) parent, text);
                                if (result != null) {
                                    return result;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
