package com.millennialmedia.intellibot.ide;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.millennialmedia.intellibot.ide.config.RobotOptionsProvider;
import com.millennialmedia.intellibot.psi.*;
import com.millennialmedia.intellibot.psi.dto.ImportType;
import com.millennialmedia.intellibot.psi.element.*;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author Stephen Abrams
 */
public class RobotCompletionContributor extends CompletionContributor {

    public static final int CELL_SEPRATOR_SPACE = 4;
    private static final TailType NEW_LINE = TailType.createSimpleTailType('\n');
    private static final TailType SUPER_SPACE = new TailType() {
        @Override
        public int processTail(Editor editor, int tailOffset) {
            Document document = editor.getDocument();
            int textLength = document.getTextLength();
            CharSequence chars = document.getCharsSequence();
            int spaceCount = 0;
            for (int i = tailOffset; i < textLength && chars.charAt(i) == ' '; i++) {
                if (++spaceCount >= CELL_SEPRATOR_SPACE)
                    break;
            }
            if (spaceCount < CELL_SEPRATOR_SPACE) {
                String toAdd = new String(new char[CELL_SEPRATOR_SPACE - spaceCount]).replace("\0", " ");
                //document.insertString(tailOffset, toAdd);
                Runnable runnable = () -> document.insertString(tailOffset, toAdd);
                WriteCommandAction.runWriteCommandAction(editor.getProject(), runnable);
            }
            return moveCaret(editor, tailOffset, CELL_SEPRATOR_SPACE);
        }
    };

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
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet results) {
                        // TODO: some brackets are only for Test Cases, some only Keywords, some both
                        PsiElement heading = getHeading(parameters.getOriginalPosition());
                        if (isInTestCases(heading) || isInKeywords(heading)) {
                            addSyntaxLookup(RobotTokenTypes.BRACKET_SETTING, results, SUPER_SPACE);
                        }
                    }
                });
        // This is the rule for adding settings and imports (Library, Test Setup)
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet results) {
                        PsiElement heading = getHeading(parameters.getOriginalPosition());
                        if (isInSettings(heading)) {
                            addSyntaxLookup(RobotTokenTypes.SETTING, results, SUPER_SPACE);
                            addSyntaxLookup(RobotTokenTypes.IMPORT, results, SUPER_SPACE);
                        }
                    }
                });
        // This is the rule for adding Gherkin (When, Then)
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet results) {
                        PsiElement heading = getHeading(parameters.getOriginalPosition());
                        if (isInTestCases(heading)) {
                            addSyntaxLookup(RobotTokenTypes.GHERKIN, results, TailType.SPACE);
                        }
                    }
                });
        // This is the rule for adding imported keywords and library methods
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement heading = getHeading(parameters.getOriginalPosition());
                        if (isInTestCases(heading) || isInKeywords(heading)) {
                            addRobotKeywords(result, parameters.getOriginalFile());
                        }
                    }
                });
        // This is the rule for adding included variable definitions
        // TODO: include variables defined in the current statement
        extend(CompletionType.BASIC,
                psiElement().inFile(psiElement(RobotFile.class)),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement heading = getHeading(parameters.getOriginalPosition());
                        if (isInTestCases(heading) || isInKeywords(heading) || isInSettings(heading)) {
                            addRobotVariables(result, parameters.getOriginalFile(), parameters.getOriginalPosition());
                        }
                    }
                });
    }

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

    private static void addRobotKeywords(CompletionResultSet result, PsiFile file) {
        if (!(file instanceof RobotFile)) {
            return;
        }
        RobotFile robotFile = (RobotFile) file;

        boolean capitalize = RobotOptionsProvider.getInstance(robotFile.getProject()).capitalizeKeywords();
        addKeywordsToResult(robotFile.getDefinedKeywords(), result, capitalize, false);

        Collection<KeywordFile> importedFiles = robotFile.getImportedFiles(-1);
        // ROBOTFRAMEWORK only import keyword from Library and Resource
        for (KeywordFile f : importedFiles) {
            if (f.getImportType() == ImportType.LIBRARY || f.getImportType() == ImportType.RESOURCE) {
                addKeywordsToResult(f.getDefinedKeywords(), result, capitalize, true);
            }
        }
    }

    private static void addRobotVariables(@NotNull CompletionResultSet result, @NotNull PsiFile file, @Nullable PsiElement position) {
        if (!(file instanceof RobotFile)) {
            return;
        }
        RobotFile robotFile = (RobotFile) file;
        addVariablesToResult(robotFile.getDefinedVariables(), result, position);

        // ROBOTFRAMEWORK only import variable from Variable and Resource
        // following code done in RobotFileImpl.getDefinedVariables()
        // bug: following will add variable in "Library xx.py" which should not
//        boolean includeTransitive = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
//        Collection<KeywordFile> importedFiles = robotFile.getImportedFiles(includeTransitive);
//        for (KeywordFile f : importedFiles) {
//            addVariablesToResult(f.getDefinedVariables(), result, position);
//        }
    }

//LookupElementBuilder.create(String): with one parameter, it is the target text, and also the lookup text
//                    .withLookupString(text): add a lookup text
//                    .withPresentableText(prompt): the text displayed in popup window
    private static void addVariablesToResult(@NotNull final Collection<DefinedVariable> variables,
                                             @NotNull final CompletionResultSet result,
                                             @Nullable PsiElement position) {
        for (DefinedVariable variable : variables) {
            if (!variable.isInScope(position)) {
                continue;
            }
            String text = variable.getLookup();
            if (text != null) {
                // we only want the first word of the variable
                String[] words = text.split("\\s+");
                String lookupString = words.length > 0 ? words[0] : text;
                LookupElement element = TailTypeDecorator.withTail(
                        LookupElementBuilder.create(lookupString)
                                .withLookupString(text)
                                .withPresentableText(lookupString)
                                .withCaseSensitivity(false),
                        TailType.NONE);
                result.addElement(element);
            }
        }
    }

    private static void addKeywordsToResult(final Collection<DefinedKeyword> keywords,
                                            final CompletionResultSet result,
                                            boolean capitalize, boolean addNamespace) {
        for (DefinedKeyword keyword : keywords) {
            String text = keyword.getKeywordName();
            String lookupString = capitalize ? WordUtils.capitalize(text) : text;
            LookupElement element = TailTypeDecorator.withTail(
                    LookupElementBuilder.create(lookupString)
                            .withLookupString(text)
                            .withPresentableText(lookupString)
                            .withCaseSensitivity(false),
                    keyword.hasArguments() ? SUPER_SPACE : TailType.NONE);
            result.addElement(element);
            if (addNamespace) {
                String ns = keyword.getNamespace() + ".";
                element = TailTypeDecorator.withTail(
                        LookupElementBuilder.create(ns + lookupString)
                                .withLookupString(ns + text)
                                .withPresentableText(ns + lookupString)
                                .withCaseSensitivity(false),
                        keyword.hasArguments() ? SUPER_SPACE : TailType.NONE);
                result.addElement(element);
            }
        }
    }

    private static void addSyntaxLookup(@NotNull RobotElementType type, @NotNull CompletionResultSet results, @NotNull TailType tail) {
        Collection<RecommendationWord> words = RobotKeywordProvider.getInstance().getRecommendationsForType(type);
        for (RecommendationWord word : words) {
            String text = word.getLookup();
            String lookupString = word.getPresentation();
            LookupElement element = TailTypeDecorator.withTail(
                    LookupElementBuilder.create(lookupString)
                            .withLookupString(text)
                            .withPresentableText(lookupString)
                            .withCaseSensitivity(false),
                    tail);
            results.addElement(element);
        }
    }

//    @Override
//    public void fillCompletionVariants(@NotNull final CompletionParameters parameters, @NotNull CompletionResultSet result) {
//        // debugging point
//        super.fillCompletionVariants(parameters, result);
//        debug("fillCompletionVariants", "called");
//    }
}