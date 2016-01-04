package com.millennialmedia.intellibot.ide.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.millennialmedia.intellibot.psi.RobotFeatureFileType;
import com.millennialmedia.intellibot.psi.util.PatternUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 * @since 2016-01-04
 */
public class RobotPythonReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    public RobotPythonReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters params, @NotNull Processor<PsiReference> processor) {
        SearchScope searchScope = params.getEffectiveSearchScope();
        if (searchScope instanceof GlobalSearchScope) {
            searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes((GlobalSearchScope) searchScope, RobotFeatureFileType.getInstance());
        }

        PsiElement element = params.getElementToSearch();
        if (element instanceof PsiNameIdentifierOwner) {
            PsiElement identifier = ((PsiNameIdentifierOwner) element).getNameIdentifier();
            if (identifier != null) {
                String text = identifier.getText();
                params.getOptimizer().searchWord(text, searchScope, UsageSearchContext.ANY, false, element);
                String keyword = PatternUtil.functionToKeyword(text);
                params.getOptimizer().searchWord(keyword, searchScope, UsageSearchContext.ANY, false, element);
            }
        }
    }
}
