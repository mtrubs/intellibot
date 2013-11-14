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

    public final static int STATE_DEFAULT = 0;
    public final static int STATE_INSIDE_GARBAGE = 1;
    public final static int STATE_INSIDE_KEYWORD = 2;
    public final static int STATE_INSIDE_VARIABLE = 3;
    public final static int STATE_DONE = 4;

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
