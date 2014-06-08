package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class SimpleInspectionVisitor extends PsiElementVisitor {

    private final ProblemsHolder holder;
    private final LocalInspectionToolSession session;
    private final SimpleInspection context;

    public SimpleInspectionVisitor(ProblemsHolder holder, LocalInspectionToolSession session, SimpleInspection context) {
        this.holder = holder;
        this.session = session;
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
