package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.dto.VariableDto;
import com.millennialmedia.intellibot.psi.util.PerformanceCollector;
import com.millennialmedia.intellibot.psi.util.PerformanceEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stephen Abrams
 */
public class KeywordDefinitionImpl extends RobotPsiElementBase implements KeywordDefinition, DefinedKeyword, PerformanceEntity {

    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?\\})(.*)");
    private static final String ANY = ".*?";
    private static final String DOT = ".";

    private Pattern pattern;
    private List<KeywordInvokable> invokedKeywords;
    private Collection<DefinedVariable> definedInlineVariables;
    private Collection<DefinedVariable> definedArguments;

    public KeywordDefinitionImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.KEYWORD_DEFINITION);
    }

    @Override
    public String getPresentableText() {
        return getTextData();
    }

    @NotNull
    @Override
    public List<KeywordInvokable> getInvokedKeywords() {
        List<KeywordInvokable> results = this.invokedKeywords;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "invoked keywords");
            results = collectInvokedKeywords();
            this.invokedKeywords = results;
            debug.complete();
        }
        return results;
    }

    private List<KeywordInvokable> collectInvokedKeywords() {
        List<KeywordInvokable> results = new ArrayList<KeywordInvokable>();
        for (PsiElement statement : getChildren()) {
            if (statement instanceof KeywordStatement) {
                for (PsiElement subStatement : statement.getChildren()) {
                    if (subStatement instanceof KeywordInvokable) {
                        results.add((KeywordInvokable) subStatement);
                    }
                }
            } else if (statement instanceof BracketSetting) {
                for (PsiElement subStatement : statement.getChildren()) {
                    if (subStatement instanceof KeywordInvokable) {
                        results.add((KeywordInvokable) subStatement);
                    }
                }
            }
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getDeclaredVariables() {
        Collection<DefinedVariable> results = new ArrayList<DefinedVariable>();
        results.addAll(getArguments());
        results.addAll(getInlineVariables());
        return results;
    }

    @NotNull
    public Collection<DefinedVariable> getInlineVariables() {
        Collection<DefinedVariable> results = this.definedInlineVariables;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "inline variables");
            results = collectInlineVariables();
            this.definedInlineVariables = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<DefinedVariable> collectInlineVariables() {
        String text = this.getPresentableText();
        if (text == null) {
            return Collections.emptySet();
        }
        Collection<DefinedVariable> results = new ArrayList<DefinedVariable>();
        int index;
        while ((index = text.indexOf("${")) > 0) {
            int close = text.indexOf("}", index);
            if (close < index) {
                break;
            }
            results.add(new VariableDto(this, text.substring(index, close + 1)));
            text = text.substring(close);
        }
        return results;
    }

    @NotNull
    private Collection<DefinedVariable> getArguments() {
        Collection<DefinedVariable> results = this.definedArguments;
        if (results == null) {
            PerformanceCollector debug = new PerformanceCollector(this, "arguments");
            results = determineArguments();
            this.definedArguments = results;
            debug.complete();
        }
        return results;
    }

    @NotNull
    private Collection<DefinedVariable> determineArguments() {
        Collection<DefinedVariable> results = new ArrayList<DefinedVariable>();
        for (PsiElement child : getChildren()) {
            if (child instanceof BracketSetting) {
                BracketSetting bracket = (BracketSetting) child;
                if (bracket.isArguments()) {
                    for (PsiElement argument : bracket.getChildren()) {
                        if (argument instanceof Argument) {
                            String text = ((Argument) argument).getPresentableText();
                            if (text != null) {
                                results.add(new VariableDto(argument, text));
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.definedArguments = null;
        this.definedInlineVariables = null;
        this.pattern = null;
        this.invokedKeywords = null;
    }

    @Override
    public boolean matches(String text) {
        if (text == null) {
            return false;
        }
        String myText = getPresentableText();
        if (myText == null) {
            return false;
        } else {
            Pattern namePattern = this.pattern;
            if (namePattern == null) {
                String myNamespace = getNamespace(getContainingFile());
                namePattern = Pattern.compile(buildPattern(myNamespace, myText.trim()), Pattern.CASE_INSENSITIVE);
                this.pattern = namePattern;
            }

            return namePattern.matcher(text.trim()).matches();
        }
    }

    @Override
    public PsiElement reference() {
        return this;
    }

    private String getNamespace(@NotNull PsiFile file) {
        String name = file.getVirtualFile().getName();
        // remove the extension
        int index = name.lastIndexOf(DOT);
        if (index > 0) {
            name = name.substring(0, index);
        }
        return name;
    }

    private String buildPattern(String namespace, String text) {
        Matcher matcher = PATTERN.matcher(text);

        String result = "";
        if (matcher.matches()) {
            String start = matcher.group(1);
            String end = buildPattern(null, matcher.group(3));

            if (start.length() > 0) {
                result = Pattern.quote(start);
            }
            result += ANY;
            if (end.length() > 0) {
                result += end;
            }
        } else {
            result = text.length() > 0 ? Pattern.quote(text) : text;
        }
        if (namespace != null && namespace.length() > 0) {
            result = "(" + Pattern.quote(namespace + DOT) + ")?" + result;
        }
        return result;
    }

    @Override
    public String getKeywordName() {
        return getPresentableText();
    }

    @Override
    public boolean hasArguments() {
        return !getArguments().isEmpty();
    }
}
