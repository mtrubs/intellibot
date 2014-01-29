package com.millennialmedia.intellibot.ide;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.millennialmedia.intellibot.psi.RobotElementType;
import com.millennialmedia.intellibot.psi.RobotKeywordProvider;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import com.millennialmedia.intellibot.psi.element.RobotFile;
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

                    for (IElementType t : RobotTokenTypes.KEYWORDS.getTypes()) {
                        keywords.addAll(RobotKeywordProvider.getInstance().getKeywordsOfType((RobotElementType) t));
                    }

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
        if (!(file instanceof RobotFile)) {
            return;
        }
        RobotFile robotFile = (RobotFile) file;

        int idx = 0;
        addKeywordsToResult(robotFile.getKeywords(), result, idx++);

        for (KeywordFile f : robotFile.getImportedFiles()) {
            addKeywordsToResult(f.getKeywords(), result, idx++);
        }
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