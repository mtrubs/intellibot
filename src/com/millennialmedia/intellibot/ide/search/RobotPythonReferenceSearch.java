package com.millennialmedia.intellibot.ide.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.millennialmedia.intellibot.psi.RobotFeatureFileType;
import com.millennialmedia.intellibot.psi.element.KeywordDefinition;
import com.millennialmedia.intellibot.psi.element.KeywordInvokable;
import com.millennialmedia.intellibot.psi.element.RobotFile;
import com.millennialmedia.intellibot.psi.element.RobotStatement;
import com.millennialmedia.intellibot.psi.util.PatternUtil;
import com.millennialmedia.intellibot.psi.util.PerformanceCollector;
import com.millennialmedia.intellibot.psi.util.PerformanceEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
        } else if (element instanceof RobotStatement) {
            if (element instanceof KeywordDefinition) {
                PerformanceCollector debug = new PerformanceCollector((PerformanceEntity) element, "ReferenceSearch");
                // TODO: this needs to be cached somehow
                Project project = params.getProject();
                Collection<VirtualFile> files = FileTypeIndex.getFiles(RobotFeatureFileType.getInstance(),
                        GlobalSearchScope.projectScope(project));
                boolean process = true;
                for (VirtualFile file : files) {
                    final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    if (psiFile instanceof RobotFile) {
                        Collection<KeywordInvokable> keywords = ((RobotFile) psiFile).getInvokedKeywords();
                        for (KeywordInvokable keyword : keywords) {
                            PsiReference reference = keyword.getReference();
                            if (reference != null && reference.isReferenceTo(element)) {
                                process = processor.process(reference);
                            }
                            // abort if we do not want to process more
                            if (!process) {
                                break;
                            }
                        }
                    }
                    // abort if we do not want to process more
                    if (!process) {
                        break;
                    }
                }
                debug.complete();
            }
            String text = ((RobotStatement) element).getPresentableText();
            params.getOptimizer().searchWord(text, searchScope, UsageSearchContext.ANY, false, element);
        }
    }
}
