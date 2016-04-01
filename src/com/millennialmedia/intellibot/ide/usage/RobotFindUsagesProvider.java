package com.millennialmedia.intellibot.ide.usage;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.element.*;
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
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        if (element instanceof Argument && element.getParent() instanceof Import) {
            // if the Argument is the first child of Import, then it is the file reference
            // everything else will be covered by Variables
            return element == element.getParent().getFirstChild();
        }
        return element instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement element) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        return RobotBundle.message("usage.declaration");
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        // this is what appears in the find usages dialog
        if (element instanceof KeywordDefinition) {
            return RobotBundle.message("usage.descriptive.keyword");
        } else if (element instanceof VariableDefinition) {
            return RobotBundle.message("usage.descriptive.variable");
        } else if (element instanceof RobotFile) {
            return RobotBundle.message("usage.descriptive.import");
        } else if (element instanceof Argument) {
            return RobotBundle.message("usage.descriptive.argument");
        }
        return "";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        // TODO: if variable definition get value set to variable
        return "";
//        if (element instanceof SimpleProperty) {
//            return ((SimpleProperty) element).getKey() + ":" + ((SimpleProperty) element).getValue();
//        } else {
//            return "";
//        }
    }
}

