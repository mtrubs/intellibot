package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stephen Abrams
 */
public class KeywordDefinitionImpl extends RobotPsiElementBase implements KeywordDefinition, DefinedKeyword {

    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?\\})(.*)");

    public KeywordDefinitionImpl(@NotNull final ASTNode node) {
        super(node, RobotTokenTypes.KEYWORD_DEFINITION);
    }

    @Override
    public String getPresentableText() {
        return getTextData();
    }

    @Override
    public boolean matches(String text) {
        String myText = getPresentableText();
        if (myText == null) {
            return text == null;
        } else {
            return Pattern.compile(buildPattern(myText), Pattern.CASE_INSENSITIVE).matcher(text).matches();
        }
    }

    private String buildPattern(String text) {
        Matcher matcher = PATTERN.matcher(text);

        if (matcher.matches()) {
            String start = matcher.group(1);
            String end = buildPattern(matcher.group(3));

            String result = "";
            if (start.length() > 0) {
                result = Pattern.quote(start);
            }
            result += ".*?";
            if (end.length() > 0) {
                result += end;
            }
            return result;
        } else {
            return text.length() > 0 ? Pattern.quote(text) : text;
        }
    }

    @Override
    public String getKeywordName() {
        return getPresentableText();
    }

    @Override
    public boolean hasArguments() {
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
