package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stephen Abrams
 */
public class KeywordDefinitionImpl extends RobotPsiElementBase implements KeywordDefinition, DefinedKeyword {

    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?\\})(.*)");
    private static final String ANY = ".*?";
    private static final String DOT = ".";

    private Boolean arguments;
    private Pattern pattern;
    private Collection<KeywordInvokable> invokedKeywords;

    public KeywordDefinitionImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.KEYWORD_DEFINITION);
    }

    @Override
    public String getPresentableText() {
        return getTextData();
    }

    @NotNull
    @Override
    public Collection<KeywordInvokable> getInvokedKeywords() {
        Collection<KeywordInvokable> results = this.invokedKeywords;
        if (results == null) {
            results = collectInvokedKeywords();
            this.invokedKeywords = results;
        }
        return results;
    }

    private Collection<KeywordInvokable> collectInvokedKeywords() {
        Collection<KeywordInvokable> results = new HashSet<KeywordInvokable>();
        for (PsiElement statement : getChildren()) {
            if (statement instanceof KeywordStatement) {
                for (PsiElement subStatement : statement.getChildren()) {
                    if (subStatement instanceof KeywordInvokable) {
                        results.add((KeywordInvokable) subStatement);
                    }
                }
            }
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.arguments = null;
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
                namePattern = Pattern.compile(buildPattern(myNamespace, myText), Pattern.CASE_INSENSITIVE);
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
        Boolean results = this.arguments;
        if (results == null) {
            results = determineArguments();
            this.arguments = results;
        }
        return results;
    }

    private boolean determineArguments() {
        for (PsiElement child : getChildren()) {
            if (child instanceof BracketSetting) {
                BracketSetting bracket = (BracketSetting) child;
                if (bracket.isArguments()) {
                    return bracket.hasArgs();
                }
            }
        }
        return false;
    }
}
