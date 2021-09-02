package com.millennialmedia.intellibot.ide.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
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
import java.util.HashSet;

/**
 * @author mrubino
 * @since 2016-01-04
 */
public class RobotPythonReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    public RobotPythonReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters params, @NotNull Processor processor) {
        SearchScope searchScope = params.getEffectiveSearchScope();
        boolean localScope = false;
        if (searchScope instanceof GlobalSearchScope) {
            searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes((GlobalSearchScope) searchScope, RobotFeatureFileType.getInstance());
        } else if (searchScope instanceof LocalSearchScope) {
            localScope = true;
        }

        PsiElement element = params.getElementToSearch();
        if (element instanceof PsiNameIdentifierOwner) {
            if (element instanceof KeywordDefinition) {
                KeywordDefinition definition = (KeywordDefinition) element;
                // this should really be part of the search options
                boolean enableInlineSearch = RobotOptionsProvider.getInstance(params.getProject()).inlineVariableSearch();
                if (definition.hasInlineVariables() && (localScope || enableInlineSearch)) {
                    processKeywordWithInline(definition, searchScope, processor, params.getProject());
                } else {
                    processRobotStatement(definition, params, searchScope);
                }
            } else {
                processPython((PsiNameIdentifierOwner) element, params, searchScope);
            }
        } else if (element instanceof RobotStatement) {
            processRobotStatement((RobotStatement) element, params, searchScope);
        }
    }

    private void processPython(@NotNull PsiNameIdentifierOwner element,
                               @NotNull ReferencesSearch.SearchParameters params,
                               @NotNull SearchScope searchScope) {
        PsiElement identifier = element.getNameIdentifier();
        if (identifier != null) {
            String text = identifier.getText();
            params.getOptimizer().searchWord(text, searchScope, UsageSearchContext.ANY, false, element);
            String keyword = PatternUtil.functionToKeyword(text);
            params.getOptimizer().searchWord(keyword, searchScope, UsageSearchContext.ANY, false, element);
        }
    }

    private void processRobotStatement(@NotNull RobotStatement element,
                                       @NotNull ReferencesSearch.SearchParameters params,
                                       @NotNull SearchScope searchScope) {
        String text = element.getPresentableText();
        params.getOptimizer().searchWord(text, searchScope, UsageSearchContext.ANY, false, element);
    }

    private void processKeywordWithInline(@NotNull KeywordDefinition element,
                                          @NotNull SearchScope searchScope,
                                          @NotNull Processor<PsiReference> processor,
                                          @NotNull Project project) {
        Collection<VirtualFile> files;

        if (searchScope instanceof LocalSearchScope) {
            files = new HashSet<VirtualFile>();
            for (PsiElement scopeElement : ((LocalSearchScope) searchScope).getScope()) {
                files.add(scopeElement.getContainingFile().getVirtualFile());
            }
        } else {
            files = FileTypeIndex.getFiles(RobotFeatureFileType.getInstance(),
                    GlobalSearchScope.projectScope(project));
        }
        processKeywordWithInline(element, processor, project, files);
    }

    private void processKeywordWithInline(@NotNull KeywordDefinition element,
                                          @NotNull Processor<PsiReference> processor,
                                          @NotNull Project project,
                                          @NotNull Collection<VirtualFile> files) {
        PerformanceCollector debug = new PerformanceCollector((PerformanceEntity) element, "ReferenceSearch");
        boolean process = true;
        for (VirtualFile file : files) {
            final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof RobotFile) {
                Collection<KeywordInvokable> keywords = ((RobotFile) psiFile).getKeywordReferences(element);
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
}
