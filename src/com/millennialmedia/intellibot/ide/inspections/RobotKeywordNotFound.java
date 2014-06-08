package com.millennialmedia.intellibot.ide.inspections;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyClass;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import com.millennialmedia.intellibot.psi.element.DefinedKeyword;
import com.millennialmedia.intellibot.psi.element.KeywordFile;
import com.millennialmedia.intellibot.psi.element.KeywordInvokable;
import com.millennialmedia.intellibot.psi.element.RobotFile;
import com.millennialmedia.intellibot.psi.ref.PythonResolver;
import com.millennialmedia.intellibot.psi.ref.RobotPythonClass;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mrubino
 * @since 2014-06-07
 */
public class RobotKeywordNotFound extends SimpleRobotInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.message("INSP.NAME.keyword.undefined");
    }

    @Override
    public boolean skip(PsiElement element) {
        return element.getNode().getElementType() != RobotTokenTypes.KEYWORD ||
                element instanceof KeywordInvokable || // this is to skip the whole keyword as opposed to just the text
                !(element.getContainingFile() instanceof RobotFile) ||
                valid(element.getText(), (RobotFile) element.getContainingFile());
    }

    @Override
    public String getMessage() {
        return RobotBundle.message("INSP.keyword.undefined");
    }

    private boolean valid(String text, RobotFile file) {
        if (text.startsWith("$")) {
            // TODO: variable declaration
            return true;
        }
        // check local file
        for (DefinedKeyword keyword : file.getKeywords()) {
            if (keywordMatches(text, buildPattern(keyword.getKeywordName()))) {
                return true;
            }
        }
        // check built in
        for (DefinedKeyword keyword : getBuildInKeywords(file)) {
            if (keywordMatches(text, buildPattern(keyword.getKeywordName()))) {
                return true;
            }
        }
        // check imports
        for (KeywordFile imported : file.getImportedFiles()) {
            for (DefinedKeyword keyword : imported.getKeywords()) {
                if (keywordMatches(text, buildPattern(keyword.getKeywordName()))) {
                    return true;
                }
            }
        }

        return false;
    }

    private static final String ROBOT_BUILT_IN = "BuiltIn";


    private Collection<DefinedKeyword> getBuildInKeywords(PsiElement element) {
        PyClass builtIn = PythonResolver.findClass(ROBOT_BUILT_IN, element.getProject());
        if (builtIn != null) {
            return new RobotPythonClass(ROBOT_BUILT_IN, builtIn).getKeywords();
        }
        return Collections.emptySet();
    }

    private boolean keywordMatches(String text, String pattern) {
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text).matches();
    }

    private Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?\\})(.*)");

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
}
