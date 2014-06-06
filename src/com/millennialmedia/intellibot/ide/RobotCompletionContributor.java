package com.millennialmedia.intellibot.ide;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.millennialmedia.intellibot.psi.RecommendationWord;
import com.millennialmedia.intellibot.psi.RobotElementType;
import com.millennialmedia.intellibot.psi.RobotKeywordProvider;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.Heading;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import com.millennialmedia.intellibot.psi.element.RobotFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author Stephen Abrams
 */
public class RobotCompletionContributor extends CompletionContributor {

    private static final TailType NEW_LINE = TailType.createSimpleTailType('\n');
    private static final TailType SUPER_SPACE = new TailType() {
        @Override
        public int processTail(Editor editor, int tailOffset) {
            Document document = editor.getDocument();
            int textLength = document.getTextLength();
            CharSequence chars = document.getCharsSequence();
            if (tailOffset < textLength - 1 && chars.charAt(tailOffset) == ' ' && chars.charAt(tailOffset + 1) == ' ') {
                // if we already have the two spaces then move the caret to after them
                return moveCaret(editor, tailOffset, 2);
            } else if (tailOffset < textLength && chars.charAt(tailOffset) == ' ') {
                // if we only have one space then add the second and move the caret after both
                document.insertString(tailOffset, " ");
                return moveCaret(editor, tailOffset, 2);
            } else {
                // if there are not spaces then add two and move the caret after them
                document.insertString(tailOffset, "  ");
                return moveCaret(editor, tailOffset, 2);
            }
        }
    };

    private static PsiElement getHeading(PsiElement current) {
        if (current == null) {
            return null;
        }
        if (current instanceof Heading) {
            return current;
        } else {
            return getHeading(current.getParent());
        }
    }

    private static boolean isInSettings(@Nullable PsiElement element) {
        boolean result = false;
        if (element instanceof Heading) {
            result = ((Heading) element).isSettings();
        }
        return result;
    }

    private static boolean isInTestCases(@Nullable PsiElement element) {
        boolean result = false;
        if (element instanceof Heading) {
            result = ((Heading) element).containsTestCases();
        }
        return result;
    }

    private static boolean isInKeywords(@Nullable PsiElement element) {
        boolean result = false;
        if (element instanceof Heading) {
            result = ((Heading) element).containsKeywordDefinitions();
        }
        return result;
    }

    public RobotCompletionContributor() {

        // This is the rule for adding Headings (*** Settings ***, *** Test Cases ***)
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet results) {
                        addSyntaxLookup(RobotTokenTypes.HEADING, results, NEW_LINE);
                    }
                });

        // This is the rule for adding Bracket Settings ([Tags], [Setup])
        // TODO: MTR: some brackets are only for Test Cases, some only Keywords, some both
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet results) {
                        PsiElement heading = getHeading(parameters.getOriginalPosition());
                        if (isInTestCases(heading) || isInKeywords(heading)) {
                            addSyntaxLookup(RobotTokenTypes.BRACKET_SETTING, results, SUPER_SPACE);
                        }
                    }
                });

        // This is the rule for adding settings and imports (Library, Test Setup)
        // TODO: MTR: Settings and Imports should only be in *** Settings ***
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet results) {
                        if (isInSettings(getHeading(parameters.getOriginalPosition()))) {
                            addSyntaxLookup(RobotTokenTypes.SETTING, results, SUPER_SPACE);
                            addSyntaxLookup(RobotTokenTypes.IMPORT, results, SUPER_SPACE);
                        }
                    }
                });

        // This is the rule for adding Gherkin (When, Then)
        // TODO: MTR: only in Test Cases
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet results) {
                        if (isInTestCases(getHeading(parameters.getOriginalPosition()))) {
                            addSyntaxLookup(RobotTokenTypes.GHERKIN, results, TailType.SPACE);
                        }
                    }
                });

        // This is the rule for adding imported keywords and library methods
        // TODO: MTR: only in test cases and keyword definitions
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        addRobotKeywords(result, parameters.getOriginalFile());
                    }
                });
    }

    @Override
    public void fillCompletionVariants(final CompletionParameters parameters, CompletionResultSet result) {
        // debugging point
        super.fillCompletionVariants(parameters, result);
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

    private static void addKeywordsToResult(final Collection<DefinedKeyword> keywords,
                                            final CompletionResultSet result,
                                            int priority) {
        for (DefinedKeyword keyword : keywords) {
            LookupElement element = TailTypeDecorator.withTail(
                    LookupElementBuilder.create(keyword.getKeywordName())
                            .withLookupString(keyword.getKeywordName())
                            .withLookupString(keyword.getKeywordName().toLowerCase())
                            .withPresentableText(keyword.getKeywordName())
                            .withCaseSensitivity(true),
                    keyword.hasArguments() ? SUPER_SPACE : TailType.NONE);
            result.addElement(PrioritizedLookupElement.withPriority(element, priority));
        }
    }

    private static void addSyntaxLookup(@NotNull RobotElementType type, @NotNull CompletionResultSet results, @NotNull TailType tail) {
        Collection<RecommendationWord> words = RobotKeywordProvider.getInstance().getRecommendationsForType(type);
        for (RecommendationWord word : words) {
            LookupElement lookup = TailTypeDecorator.withTail(
                    LookupElementBuilder.create(word.getPresentation())
                            .withLookupString(word.getLookup())
                            .withLookupString(word.getLookup().toLowerCase())
                            .withPresentableText(word.getPresentation())
                            .withCaseSensitivity(true),
                    tail);
            results.addElement(lookup);
        }
    }
}