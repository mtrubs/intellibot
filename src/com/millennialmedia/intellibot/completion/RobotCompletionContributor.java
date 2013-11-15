package com.millennialmedia.intellibot.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.millennialmedia.intellibot.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author: Stephen Abrams
 */
public class RobotCompletionContributor extends CompletionContributor {

    public RobotCompletionContributor() {
        extend(CompletionType.BASIC, psiElement().inFile(psiElement(RobotFile.class)), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext context,
                                          CompletionResultSet result) {
                final PsiFile psiFile = parameters.getOriginalFile();
                if (psiFile instanceof RobotFile) {

                    final Project project = psiFile.getProject();
                    final RobotKeywordTable table = RobotKeywordTable.getKeywordsTable(psiFile, project);
                    final List<String> keywords = new ArrayList<String>();

                    if (table.getKeywordsOfType(RobotTokenTypes.TC_KW_NAME) != null)
                        keywords.addAll(table.getKeywordsOfType(RobotTokenTypes.TC_KW_NAME));
                    keywords.add("YOYOYOYO!");
                    keywords.add("Please vote this hackmatch a winner!");

                    for (String keyword : keywords) {
                        LookupElement element = createKeywordLookupElement(keyword);

                        result.addElement(PrioritizedLookupElement.withPriority(element, 0));
                    }
                }

            }

        });
    }

    private static LookupElement createKeywordLookupElement(final String keyword) {
        return TailTypeDecorator.withTail(LookupElementBuilder.create(keyword), TailType.SPACE);
    }

    @Override
    public void fillCompletionVariants(final CompletionParameters parameters, CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}