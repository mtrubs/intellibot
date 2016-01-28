package com.millennialmedia.intellibot.ide.usage;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.PsiNamedElementUsageGroupBase;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.impl.FileStructureGroupRuleProvider;
import com.intellij.usages.rules.PsiElementUsage;
import com.intellij.usages.rules.UsageGroupingRule;
import com.millennialmedia.intellibot.psi.element.KeywordDefinitionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 * @since 2016-01-28
 */
public class RobotKeywordGroupingRuleProvider implements FileStructureGroupRuleProvider {

    @Nullable
    @Override
    public UsageGroupingRule getUsageGroupingRule(Project project) {
        return new RobotKeywordGroupingRule();
    }

    private static class RobotKeywordGroupingRule implements UsageGroupingRule {

        private RobotKeywordGroupingRule() {
        }

        public UsageGroup groupUsage(@NotNull Usage usage) {
            if (!(usage instanceof PsiElementUsage)) {
                return null;
            } else {
                PsiElement psiElement = ((PsiElementUsage) usage).getElement();
                KeywordDefinitionImpl definition = PsiTreeUtil.getParentOfType(psiElement, KeywordDefinitionImpl.class, false);
                return definition == null ? null : new PsiNamedElementUsageGroupBase<KeywordDefinitionImpl>(definition);
            }
        }
    }
}
