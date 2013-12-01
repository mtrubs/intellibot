package com.millennialmedia.intellibot.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.millennialmedia.intellibot.psi.RobotFile;
import com.millennialmedia.intellibot.psi.RobotKeywordProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author Stephen Abrams
 */
public class RobotCompletionContributor extends CompletionContributor {

    public RobotCompletionContributor() {

        // SMA todo: better honed "places"

        extend(CompletionType.BASIC, psiElement().inFile(psiElement(RobotFile.class)), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                final PsiFile psiFile = parameters.getOriginalFile();
                if (psiFile instanceof RobotFile) {
                    final List<String> keywords = new ArrayList<String>();

                    keywords.addAll(new RobotKeywordProvider().getAllKeywords());
                    for (String keyword : keywords) {
                        LookupElement element = createKeywordLookupElement(keyword);

                        result.addElement(PrioritizedLookupElement.withPriority(element, 0));
                    }


                }

            }

        });

        extend(CompletionType.BASIC, psiElement().inFile(psiElement(RobotFile.class)), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                addRobotKeywords(result, parameters.getOriginalFile());
            }
        });
    }

    private static void addRobotKeywords(CompletionResultSet result, PsiFile file) {
        if (!(file instanceof RobotFile)) return;
        final RobotFile robotFile = (RobotFile) file;

        addKeywordsToResult(robotFile.getKeywords(), result, 0);
    }

    private static void addKeywordsToResult(final Collection<String> keywords,
                                            final CompletionResultSet result,
                                            int priority) {
        for (String keyword : keywords) {
            LookupElement element = createKeywordLookupElement(keyword);

            result.addElement(PrioritizedLookupElement.withPriority(element, priority));
        }
    }

    private static LookupElement createKeywordLookupElement(final String keyword) {
        return TailTypeDecorator.withTail(LookupElementBuilder.create(keyword), TailType.SPACE);
    }

    @Override
    public void fillCompletionVariants(final CompletionParameters parameters, CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}