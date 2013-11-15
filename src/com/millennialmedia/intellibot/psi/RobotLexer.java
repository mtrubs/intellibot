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
    public final static int IN_KEYWORD = 5;
    public final static int IN_TEST_DEF = 6;
    public final static int IN_ARG_KEYWORD = 7;
    public final static int IN_ARG_TEST_DEF = 8;
    public final static int IN_ARG_SETTING = 9;
    public final static int IN_KEYWORD_MAYBE = 10;

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
                goToEndOfLine();
                return;
            } else if (isNewLine()) {
                myStartOffset++;
                myPosition++;
                advance();
                return;
            } else if (isHeading()) {
                myCurrentToken = RobotTokenTypes.HEADING;
                String nextWord = subSequence(myPosition, nextIndexOf('\n'));
                if (isSettings(nextWord)) {
                    myState = IN_SETTINGS_HEADER;
                } else if (isTestCases(nextWord)) {
                    myState = IN_TEST_CASES_HEADER;
                } else if (isKeywords(nextWord)) {
                    myState = IN_KEYWORDS_HEADER;
                } else {
                    myCurrentToken = RobotTokenTypes.ERROR;
                }
                goToEndOfLine();
                return;
            } else {
                myCurrentToken = RobotTokenTypes.ERROR;
            }
            return;
        } else if (myState == IN_SETTINGS_HEADER) {
            if (isComment()) {
                myCurrentToken = RobotTokenTypes.COMMENT;
                goToEndOfLine();
                return;
            } else if (isNewLine()) {
                myStartOffset++;
                myPosition++;
                advance();
                return;
            } else if (isHeading()) {
                myState = IN_START;
                advance();
                return;
            }
            String nextWord = getNextWord();
            if (isImport(nextWord)) {
                myState = IN_IMPORT;
                myCurrentToken = RobotTokenTypes.IMPORT;
                goToNextNewLineOrSuperSpace();
                return;
            } else if (isGlobalSettings(nextWord)) {
                if (myKeywordProvider.getSettingsFollowedByKeywords().contains(nextWord)) {
                    myState = IN_KEYWORD_MAYBE;
                    advance();
                    return;
                } else if (myKeywordProvider.getSettingsFollowedByStrings().contains(nextWord)) {
                    myCurrentToken = RobotTokenTypes.SETTING;
                    myState = IN_ARG_SETTING;
                    goToNextNewLineOrSuperSpace();
                    return;
                }
            } else {
                // TODO: err?
                goToEndOfLine();
                return;
            }
        } else if (myState == IN_IMPORT) {
            if (areAtStartOfSuperSpace()) {
                skipWhitespace();
                advance();
                return;
            }
            myCurrentToken = RobotTokenTypes.ARGUMENT;
            myState = IN_SETTINGS_HEADER;
            goToEndOfLine();
            return;
        } else if (myState == IN_TEST_CASES_HEADER || myState == IN_KEYWORDS_HEADER) {
            if (isComment()) {
                myCurrentToken = RobotTokenTypes.COMMENT;
                goToEndOfLine();
                return;
            } else if (isNewLine()) {
                myStartOffset++;
                myPosition++;
                advance();
                return;
            } else if (isHeading()) {
                myState = IN_START;
                advance();
                return;
            } else if (areAtStartOfSuperSpace()) {
                myCurrentToken = RobotTokenTypes.ERROR;
                goToEndOfLine();
            } else {
                myCurrentToken = RobotTokenTypes.TC_KW_NAME;
                myState = IN_TEST_DEF;
                goToEndOfLine();
            }
            return;
        } else if (myState == IN_KEYWORD) {
            if (areAtStartOfSuperSpace()) {
                skipWhitespace();
                advance();
                return;
            } else if (areAtStartOfSpace()) {
                skipWhitespace();
                advance();
                return;
            }
            myCurrentToken = RobotTokenTypes.KEYWORD;
            if (!isNewLine()) {
                goToEndOfLine();
                myState = IN_TEST_DEF;
            } else {
                //we can have spaces after a keyword but not before an arg
                myState = IN_ARG_KEYWORD;
            }
            return;
        } else if (myState == IN_ARG_KEYWORD || myState == IN_ARG_TEST_DEF || myState == IN_ARG_SETTING) {
            if (areAtStartOfSuperSpace()) {
                skipWhitespace();
                advance();
                return;
            }
            myCurrentToken = RobotTokenTypes.ARGUMENT;
            goToEndOfLine();
            //we're done with args, pop to previous state based on current state, thanks lack of state stack
            if (myState == IN_ARG_KEYWORD) {
                myState = IN_KEYWORD;
            } else if (myState == IN_ARG_TEST_DEF) {
                myState = IN_TEST_DEF;
            } else if (myState == IN_ARG_SETTING) {
                myState = IN_SETTINGS_HEADER;
            }
            return;
        } else if (myState == IN_TEST_DEF) {
            if (areAtStartOfSuperSpace()) {
                skipWhitespace();
                advance();
                return;
            } else if (isNewLine()) {
                myStartOffset++;
                myPosition++;
                advance();
                return;
            } else if (isHeading()) {
                myState = IN_START;
                advance();
                return;
            }

            String nextWord = getNextWord();
            if (myKeywordProvider.getKeywordsOfType(RobotTokenTypes.BRACKET_SETTING).contains(nextWord)) {
                goToNextNewLineOrSuperSpace();
                if (myKeywordProvider.getSettingsFollowedByStrings().contains(nextWord)) {
                    myState = IN_ARG_TEST_DEF;
                    myCurrentToken = RobotTokenTypes.BRACKET_SETTING;
                    return;
                } else if (myKeywordProvider.getSettingsFollowedByKeywords().contains(nextWord)) {
                    myState = IN_KEYWORD;
                    myCurrentToken = RobotTokenTypes.BRACKET_SETTING;
                    return;
                }
            } else {
                myState = IN_KEYWORD_MAYBE;
                advance();
                return;
            }
            myCurrentToken = RobotTokenTypes.ERROR;
            return;
        } else if (myState == IN_KEYWORD_MAYBE) {
            myCurrentToken = RobotTokenTypes.GHERKIN;
            goToStartOfNextWhiteSpace();
            myState = IN_KEYWORD;
            return;
        }

        myCurrentToken = RobotTokenTypes.ERROR;
        goToEndOfLine();
    }

    private boolean isComment() {
        return charAtEquals(myPosition, '#');
    }

    private boolean isNewLine() {
        return charAtEquals(myPosition, '\n');
    }

    private boolean isHeading() {
        return charAtEquals(myPosition, '*');
    }

    private boolean isSettings(String nextWord) {
        return "*** Settings ***".equals(nextWord) || "*** Setting ***".equals(nextWord);
    }

    private String subSequence(int start, int end) {
        return end < myEndOffset ? myBuffer.subSequence(start, end).toString() : null;
    }

    private boolean isImport(String nextWord) {
        return myKeywordProvider.getKeywordsOfType(RobotTokenTypes.IMPORT).contains(nextWord);
    }

    private boolean isGlobalSettings(String nextWord) {
        return myKeywordProvider.getGlobalSettings().contains(nextWord);
    }

    private boolean isTestCases(String nextWord) {
        return "*** Test Cases ***".equals(nextWord) || "*** Test Case ***".equals(nextWord);
    }

    private boolean isKeywords(String nextWord) {
        return "*** Keywords ***".equals(nextWord) || "*** Keyword ***".equals(nextWord);
    }

    private void goToEndOfLine() {
        while (!isNewLine()) {
            myPosition++;
        }
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

    private int nextIndexOf(char target) {
        int position = myPosition;
        while (!charAtEquals(position, target)) {
            position++;
        }
        return position;
    }

    private String getNextWord() {
        int nextSpace = indexOfNextSuperSpaceOrNewLine();
        return nextSpace <= myEndOffset ? myBuffer.subSequence(myPosition, nextSpace).toString() : null;
    }

    private void goToNextNewLineOrSuperSpace() {
        while (myPosition <= myBuffer.length() && !areAtStartOfSuperSpace() && !isNewLine()) {
            myPosition++;
        }
    }

    private boolean areAtStartOfSuperSpace() {
        return (charAtEquals(myPosition, ' ') && charAtEquals(myPosition + 1, ' '))
                || charAtEquals(myPosition, '\t');
    }

    private boolean areAtStartOfSpace() {
        return charAtEquals(myPosition, ' ');
    }

    private void goToStartOfNextWhiteSpace() {
        while (myPosition < myBuffer.length() && !Character.isWhitespace(myBuffer.charAt(myPosition))) {
            myPosition++;
        }
    }

    private void goToNextThingAfterSuperSpace() {
        while (myPosition < myBuffer.length() && Character.isWhitespace(myBuffer.charAt(myPosition))) {
            myPosition++;
        }
    }

    private void skipWhitespace() {
        while (myPosition < myBuffer.length() && Character.isWhitespace(myBuffer.charAt(myPosition))) {
            myPosition++;
            myStartOffset++;
        }
    }

    private int indexOfNextSuperSpaceOrNewLine() {
        int position = myPosition;
        while (position < myBuffer.length() && !isSuperSpace(position) && !isNewLine()) {
            position++;
        }
        return position;
    }

    private boolean isSuperSpace(int index) {
        return (charAtEquals(index, ' ') && charAtEquals(index + 1, ' '))
                || (charAtEquals(index, '\t'));
    }

    private boolean charAtEquals(int index, char c) {
        return index < myBuffer.length() && myBuffer.charAt(index) == c;
    }
}
