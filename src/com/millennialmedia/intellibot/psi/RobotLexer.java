package com.millennialmedia.intellibot.psi;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RobotLexer extends LexerBase {

    protected CharSequence myBuffer = ArrayUtil.EMPTY_CHAR_SEQUENCE;
    protected int myStartOffset = 0;
    protected int myEndOffset = 0;
    private int myPosition;
    private IElementType myCurrentToken;
    private int myCurrentTokenStart;
    private List<String> myKeywords;
    private int myState;

    private final RobotKeywordProvider myKeywordProvider;
    private String myCurLanguage;

    public final static int IN_START = 0;
    public final static int IN_SETTINGS_HEADER = 1;
    public final static int IN_TEST_CASES_HEADER = 2;
    public final static int IN_KEYWORDS_HEADER = 3;
    public final static int IN_IMPORT = 4;

    private enum State {
        InSettings;
    }

    public RobotLexer(RobotKeywordProvider provider) {
        myKeywordProvider = provider;
        updateLanguage("en");
    }

    private void updateLanguage(String language) {
        myCurLanguage = language;
        myKeywords = new ArrayList<String>(myKeywordProvider.getAllKeywords(language));
        Collections.sort(myKeywords, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
    }

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        myBuffer = buffer;
        myStartOffset = startOffset;
        myEndOffset = endOffset;
        myPosition = startOffset;
        myState = initialState;
        advance();
    }

    @Nullable
    public static String fetchLocationLanguage(final @NotNull String commentText) {
        if (commentText.startsWith("language:")) {
            return commentText.substring(9).trim();
        }
        return null;
    }

    @Override
    public void advance() {
        if (myPosition >= myEndOffset) {
            myCurrentToken = null;
            return;
        }

        myCurrentTokenStart = myPosition;
        if (myState == IN_START) {
            // comment
            if (isComment()) {
                myCurrentToken = RobotTokenTypes.COMMENT;
                goToStartOfNextLine();
                return;
            } else if (isHeading()) {
                myCurrentToken = RobotTokenTypes.HEADING;

                if (isSettings()) {
                    myState = IN_SETTINGS_HEADER;
                } else if (isTestCases()) {
                    myState = IN_TEST_CASES_HEADER;
                } else if (isKeywords()) {
                    myState = IN_KEYWORDS_HEADER;
                } else {
                    // TODO: err?
                }

                goToStartOfNextLine();
                return;
            } else {
                // TODO: err?
            }
        } else if (myState == IN_SETTINGS_HEADER) {
            if (isImport()) {
                myState = IN_IMPORT;
                myCurrentToken = RobotTokenTypes.IMPORT;
            }
        } else if (myState == IN_TEST_CASES_HEADER) {

        } else if (myState == IN_KEYWORDS_HEADER) {

        }
        // TODO: not right type
        myCurrentToken = RobotTokenTypes.KEYWORD;
        goToStartOfNextLine();
    }

    private boolean isComment() {
        char c = myBuffer.charAt(myPosition);
        return c == '#';
    }

    private boolean isHeading() {
        char c = myBuffer.charAt(myPosition);
        return c == '*';
    }

    private boolean isSettings() {
        return false;
    }

    private boolean isImport() {
        return false;
    }

    private boolean isTestCases() {
        return false;
    }

    private boolean isKeywords() {
        return false;
    }

    public void goToStartOfNextLine() {
        while (myPosition < myEndOffset && myBuffer.charAt(myPosition) != '\n') {
            myPosition++;
        }
        //we now know the charAt(myPosition) is a \n, so we go one more and we're at the start of the next line
        myPosition++;
    }

    @Override
    public int getState() {
        return myState;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return myCurrentToken;
    }

    @Override
    public int getTokenStart() {
        return myCurrentTokenStart;
    }

    @Override
    public int getTokenEnd() {
        return myPosition;
    }

    @Override
    public CharSequence getBufferSequence() {
        return myBuffer;
    }

    @Override
    public int getBufferEnd() {
        return myEndOffset;
    }
}
