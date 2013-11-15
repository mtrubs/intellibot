package com.millennialmedia.intellibot.psi;

import java.util.*;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public final static int IN_KEYWORD_MAYBE = 9;

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
                    myCurrentToken = RobotTokenTypes.ERROR;
                }
                goToStartOfNextLine();
                return;
            } else {
                myCurrentToken = RobotTokenTypes.ERROR;
            }
            return;
        } else if (myState == IN_SETTINGS_HEADER) {
            if (isImport()) {
                myState = IN_IMPORT;
                myCurrentToken = RobotTokenTypes.IMPORT;
                goToStartOfNextLine();
                return;
            }
            if(isGlobalSettings()){
                if(myKeywordProvider.getSettingsFollowedByKeywords().contains(getNextWord())){
                    myState = IN_KEYWORD_MAYBE;
                    goToStartOfNextLine();
                    return;
                } else if (myKeywordProvider.getSettingsFollowedByStrings().contains(getNextWord())) {
                    myState = IN_ARG_SETTING;
                    goToStartOfNextLine();
                    return;
                }
            }
            return;
        } else if (myState == IN_IMPORT) {
            myCurrentToken = RobotTokenTypes.ARGUMENT;
            myState = IN_SETTINGS_HEADER;
            goToStartOfNextLine();
            return;
        } else if (myState == IN_TEST_CASES_HEADER || myState == IN_KEYWORDS_HEADER) {
            if(areAtStartOfSuperSpace()){
                myCurrentToken = RobotTokenTypes.ERROR;
                goToStartOfNextLine();
            } else {
                myCurrentToken = RobotTokenTypes.TC_KW_NAME;
                myState = IN_TEST_DEF;
                goToStartOfNextLine();
            }
            return;
        } else if (myState == IN_KEYWORD) {
            goToNextNewLineOrSuperSpace();
            if (areAtStartOfSuperSpace()) {
                goToNextThingAfterSuperSpace();
            }
            myCurrentToken = RobotTokenTypes.KEYWORD;
            if (myBuffer.charAt(myPosition) == '\n') {
                goToStartOfNextLine();
                myState = IN_TEST_DEF;
            } else {
                //we can have spaces after a keyword but not before an arg
                myState = IN_ARG_KEYWORD;
            }
            return;
        } else if (myState == IN_ARG_KEYWORD || myState == IN_ARG_TEST_DEF || myState == IN_ARG_SETTING) {
            myCurrentToken = RobotTokenTypes.ARGUMENT;
            goToNextNewLineOrSuperSpace();
            if (areAtStartOfSuperSpace()) {
                goToNextThingAfterSuperSpace();
            } else if (myBuffer.charAt(myPosition) == '\n') {
                //we're done with args, pop to previous state based on current state, thanks lack of state stack
                if (myState == IN_ARG_KEYWORD) {
                    myState = IN_KEYWORD;
                } else if (myState == IN_ARG_TEST_DEF) {
                    myState = IN_TEST_DEF;
                } else if (myState == IN_ARG_SETTING) {
                    myState = IN_SETTINGS_HEADER;
                }
            }
            return;
        } else if (myState == IN_TEST_DEF) {
            if (!areAtStartOfSuperSpace()) {
                myState = IN_TEST_CASES_HEADER;
                return;
            }
            if (myBuffer.charAt(myPosition) == '\n') {
                goToStartOfNextLine();
                return;
            }
            if (areAtStartOfSuperSpace()) {
                goToNextThingAfterSuperSpace();
                if (myBuffer.charAt(myPosition) == '\n') {
                    goToStartOfNextLine();
                    return;
                }
                String nextWord = getNextWord();
                if (myKeywordProvider.getKeywordsOfType(RobotTokenTypes.BRACKET_SETTING).contains(nextWord)) {
                    myPosition = myPosition + nextWord.length(); //go after the [thing]
                    goToNextThingAfterSuperSpace();
                    if (myKeywordProvider.getSettingsFollowedByStrings().contains(nextWord)) {
                        myState = IN_ARG_TEST_DEF;
                        return;
                    } else if (myKeywordProvider.getSettingsFollowedByKeywords().contains(nextWord)) {
                        myState = IN_KEYWORD;
                        return;
                    }
                } else {
                    myState = IN_KEYWORD_MAYBE;
                    return;
                }
            }
            myCurrentToken = RobotTokenTypes.ERROR;
            return;
        } else if (myState == IN_KEYWORD_MAYBE) {
            myCurrentToken = RobotTokenTypes.GHERKIN;
            goToStartOfNextWhiteSpace();
            myState = IN_KEYWORD;
        }

        myCurrentToken = RobotTokenTypes.ERROR;
        goToStartOfNextLine();
    }

    private int nextIndexOfChar(char target) {
        int position = myPosition;
        char c = myBuffer.charAt(myPosition);
        while (c != target) {
            position++;
        }
        return position;
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
        String nextWord = getNextWord();
        if(nextWord.equals("*** Settings ***") || nextWord == "*** Setting ***"){
            return true;
        }
        return false;
    }

    private boolean isImport() {
        String nextWord = getNextWord();
        if(myKeywordProvider.getKeywordsOfType(RobotTokenTypes.IMPORT).contains(nextWord)){
            return true;
        }
        return false;
    }

    private boolean isGlobalSettings() {
        String nextWord = getNextWord();
        if(myKeywordProvider.getGlobalSettings().contains(nextWord)){
            return true;
        }
        return false;
    }

    private boolean isTestCases() {
        String nextWord = getNextWord();
        if(nextWord.equals("*** Test Cases ***") || nextWord == "*** Test Case ***"){
            return true;
        }
        return false;
    }

    private boolean isKeywords() {
        String nextWord = getNextWord();
        if(nextWord.equals("*** Keywords ***") || nextWord == "*** Keyword ***"){
            return true;
        }
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
        return myPosition - 1;
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
        while (position < myEndOffset && myBuffer.charAt(position) != target) {
            position++;
        }
        return position;
    }

    private String getNextWord() {
        return myBuffer.subSequence(myPosition, nextIndexOf(' ') + 1).toString();
    }

    private void goToNextNewLineOrSuperSpace() {
        while (myPosition < myEndOffset && !areAtStartOfSuperSpace() && myBuffer.charAt(myPosition) != '\n') {
            myPosition++;
        }
    }

    private boolean areAtStartOfSuperSpace() {
        return (myPosition + 1 < myEndOffset && myBuffer.charAt(myPosition) == ' ' && myBuffer.charAt(myPosition + 1) == ' ')
                || myBuffer.charAt(myPosition) == '\t';
    }

    public void goToStartOfNextWhiteSpace() {
        while (myPosition < myEndOffset && !Character.isWhitespace(myBuffer.charAt(myPosition))) {
            myPosition++;
        }
    }

    public void goToNextThingAfterSuperSpace() {
        while (myPosition < myEndOffset && Character.isWhitespace(myBuffer.charAt(myPosition))) {
            myPosition++;
        }
        myPosition++;
    }
}
