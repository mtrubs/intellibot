package com.millennialmedia.intellibot.ide;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import com.millennialmedia.intellibot.psi.RobotKeywordProvider;
import com.millennialmedia.intellibot.psi.RobotLexer;
import com.millennialmedia.intellibot.psi.element.KeywordDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class RobotFindUsagesProvider implements FindUsagesProvider {

    private static final String EMPTY = "";

    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new RobotLexer(RobotKeywordProvider.getInstance()), TokenSet.EMPTY, TokenSet.EMPTY, TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof KeywordDefinition;
    }

    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "reference.dialogs.findUsages.other";
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        return element instanceof KeywordDefinition ? "keyworddefinition" : element.toString();
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        String name = null;
        if (element instanceof PsiNamedElement) {
            name = ((PsiNamedElement) element).getName();
        }
        return name == null ? EMPTY : name;
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}

