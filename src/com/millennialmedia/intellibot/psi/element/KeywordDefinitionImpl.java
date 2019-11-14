package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.millennialmedia.intellibot.ide.icons.RobotIcons;
import com.millennialmedia.intellibot.psi.util.PerformanceCollector;
import com.millennialmedia.intellibot.psi.util.PerformanceEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stephen Abrams
 */
public class KeywordDefinitionImpl extends RobotPsiElementBase implements KeywordDefinition, DefinedKeyword, PerformanceEntity, PsiNameIdentifierOwner {

    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?\\})(.*)");
    private static final String ANY = ".*?";
    private static final String DOT = ".";

    private Pattern pattern;
    private List<KeywordInvokable> invokedKeywords;
    private Collection<DefinedVariable> definedInlineVariables;
    private Collection<DefinedVariable> definedArguments;
    private String namespace;

    public KeywordDefinitionImpl(@NotNull final ASTNode node) {
        super(node);
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
            if (statement instanceof KeywordStatement || statement instanceof BracketSetting) {
                //noinspection unchecked
                results.addAll(PsiTreeUtil.collectElementsOfType(statement, KeywordInvokable.class));
            }
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getDeclaredVariables() {
        Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();
        results.addAll(getArguments());
        results.addAll(getInlineVariables());
        return results;
    }

    @Override
    public boolean hasInlineVariables() {
        return getInlineVariables().size() > 0;
    }

    @NotNull
    private Collection<DefinedVariable> getInlineVariables() {
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
        Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();
        for (PsiElement child : getChildren()) {
            if (child instanceof KeywordDefinitionId) {
                for (PsiElement grandChild : child.getChildren()) {
                    if (grandChild instanceof DefinedVariable) {
                        results.add((DefinedVariable) grandChild);
                    }
                }
            }
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
        Collection<DefinedVariable> results = new LinkedHashSet<DefinedVariable>();
        for (PsiElement child : getChildren()) {
            if (child instanceof BracketSetting) {
                BracketSetting bracket = (BracketSetting) child;
                if (bracket.isArguments()) {
                    for (PsiElement argument : bracket.getChildren()) {
                        if (argument instanceof Argument) {
                            for (PsiElement definition : argument.getChildren()) {
                                if (definition instanceof DefinedVariable) {
                                    results.add((DefinedVariable) definition);
                                }
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
        Pattern namePattern = this.pattern;
        if (namePattern == null) {
            String myNamespace = getNamespace();
            namePattern = Pattern.compile(buildPattern(myNamespace, myText.trim()), Pattern.CASE_INSENSITIVE);
            this.pattern = namePattern;
        }
        text = text.trim();
        if (namePattern.matcher(text).matches())
            return true;
        int p = text.lastIndexOf('.');
        if (p >= 0) {
            String lib = text.substring(0, p);
            String kw = text.substring(p+1).replaceAll("[_ ]", "");
            text = lib + "." + kw;
        } else {
            text = text.replaceAll("[_ ]", "");
        }
        return namePattern.matcher(text).matches();
    }

    @Override
    public PsiElement reference() {
        return this;
    }

    @Override
    public String getNamespace() {
        String name = this.namespace;
        if (name == null) {
            PsiFile file = this.getContainingFile();
            if (file != null) {
                name = file.getName();
                // remove the extension
                int index = name.lastIndexOf(DOT);
                if (index > 0) {
                    name = name.substring(0, index);
                }
            }
            this.namespace = name;
        }
        return name;
    }

    // Keyword names used in the test data are compared with method names to find the method implementing these keywords.
    // Name comparison is case-insensitive, and also spaces and underscores are ignored.
    // For example, the method hello maps to the keyword name Hello, hello or even h e l l o.
    // Similarly both the do_nothing and doNothing methods can be used as the Do Nothing keyword in the test data.
    //   Embedding arguments into keyword name
    // These kind of keywords are also used the same way as other keywords except that spaces and underscores are not ignored in their names.
    // They are, however, case-insensitive like other keywords.
    // For example, the keyword in the example above could be used like select x from list, but not like Select x fromlist.
    private String buildPattern(String namespace, String text) {
        Matcher matcher = PATTERN.matcher(text);

        String result = "";
        if (matcher.matches()) {
            result = buildPatternEmbedding(text);
        } else {
            result = text.length() > 0 ? Pattern.quote(text.replaceAll("[_ ]", "")) : text;
        }
        if (namespace != null && namespace.length() > 0) {
            result = "(" + Pattern.quote(namespace + DOT) + ")?" + result;
        }
        return result;
    }

    private String buildPatternEmbedding(String text) {
        Matcher matcher = PATTERN.matcher(text);

        String result = "";
        if (matcher.matches()) {
            String start = matcher.group(1);
            String end = buildPatternEmbedding(matcher.group(3));

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

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return PsiTreeUtil.findChildOfType(this, KeywordDefinitionId.class);
    }

    @Override
    @NotNull
    public Icon getIcon(int flags) {
        Heading heading = PsiTreeUtil.getParentOfType(this, Heading.class);
        if (heading != null && heading.containsTestCases()) {
            return RobotIcons.TEST_CASE;
        } else {
            return RobotIcons.KEYWORD_DEFINITION;
        }
    }
}
