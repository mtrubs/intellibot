package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class SimpleInspectionVisitor extends PsiElementVisitor {

    private final ProblemsHolder holder;
    private final SimpleInspection context;

    public SimpleInspectionVisitor(ProblemsHolder holder, SimpleInspection context) {
        this.holder = holder;
        this.context = context;
    }

    @Override
    public void visitElement(PsiElement element) {
        if (this.context.skip(element)) {
            return;
        }

        // TODO: the refactor
        this.holder.registerProblem(element, this.context.getMessage());
    }
}
