package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.RecommendationWord;
import com.millennialmedia.intellibot.psi.RobotKeywordProvider;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class RobotGherkinInspection extends RobotInspection {

    public boolean allowUppercase = false;

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.message("INSP.NAME.gherkin.format");
    }

    @Override
    public JComponent createOptionsPanel() {
        MultipleCheckboxOptionsPanel panel = new MultipleCheckboxOptionsPanel(this);
        panel.addCheckbox(RobotBundle.message("INSP.OPT.gherkin.fotmat.upper"), "allowUppercase");
        return panel;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
                                          boolean isOnTheFly,
                                          @NotNull LocalInspectionToolSession session) {
        return new Visitor(holder, session, this.allowUppercase);
    }

    private static class Visitor extends PsiElementVisitor {

        private static final Collection<String> NORMAL;
        private static final Collection<String> UPPER;

        static {
            Collection<RecommendationWord> gherkin = RobotKeywordProvider.getInstance().getRecommendationsForType(RobotTokenTypes.GHERKIN);

            NORMAL = new HashSet<String>();
            UPPER = new HashSet<String>();

            for (RecommendationWord word : gherkin) {
                NORMAL.add(word.getPresentation());
                UPPER.add(word.getPresentation().toUpperCase());
            }
        }

        private final ProblemsHolder holder;
        private final LocalInspectionToolSession session;
        private final boolean allowUppercase;

        private Visitor(ProblemsHolder holder, LocalInspectionToolSession session, boolean allowUppercase) {
            this.holder = holder;
            this.session = session;
            this.allowUppercase = allowUppercase;
        }

        public void visitElement(PsiElement element) {
            if (element.getNode().getElementType() != RobotTokenTypes.GHERKIN) {
                return;
            } else if (valid(element.getText())) {
                return;
            }
            
            // TODO: the refactor
            this.holder.registerProblem(element, RobotBundle.message("INSP.gherkin.format"));
        }

        private boolean valid(String text) {
            return NORMAL.contains(text) || (this.allowUppercase && UPPER.contains(text));
        }
    }
}
