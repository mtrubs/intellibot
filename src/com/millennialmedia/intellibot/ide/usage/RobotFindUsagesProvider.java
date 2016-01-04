package com.millennialmedia.intellibot.ide.usage;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.millennialmedia.intellibot.psi.element.Argument;
import com.millennialmedia.intellibot.psi.element.Import;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stephen Abrams
 */
public class RobotFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new RobotWordScanner();
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        if (psiElement instanceof Argument && psiElement.getParent() instanceof Import) {
            // if the import is the same as the
            return psiElement == psiElement.getParent().getFirstChild();
        }
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        // TODO: determines how to group the search results
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        // TODO: is show for the definition element
        return "Definition";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        // TODO: is what is shown in the search results
//        if (element instanceof RobotStatement) {
//            return ((RobotStatement) element).getPresentableText();
//        } else {
//            return "";
//        }
        return "dummy";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        // TODO: if variable definition get value set to variable
        return "mtr";
//        if (element instanceof SimpleProperty) {
//            return ((SimpleProperty) element).getKey() + ":" + ((SimpleProperty) element).getValue();
//        } else {
//            return "";
//        }
    }
}

