package com.millennialmedia.intellibot.psi;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class RobotLexer extends LexerBase {

    private CharSequence buffer = ArrayUtil.EMPTY_CHAR_SEQUENCE;
    private int startOffset;
    private int endOffset;
    private int position;
    private IElementType currentToken;

    private final RobotKeywordProvider keywordProvider;

    private Stack<Integer> level = new Stack<Integer>();
    protected static final int SETTINGS_HEADING = 1;
    protected static final int TEST_CASES_HEADING = 2;
    protected static final int KEYWORDS_HEADING = 3;
    protected static final int VARIABLES_HEADING = 4;
    protected static final int IMPORT = 5;
    protected static final int KEYWORD = 6;
    protected static final int ARG = 7;
    protected static final int KEYWORD_DEFINITION = 8;
    protected static final int VARIABLE_DEFINITION = 9;
    protected static final int SYNTAX = 10;
    protected static final int GHERKIN = 11;
    // TODO: we might run into max int issues at some point
    private static final int RATE = 12; // this should always be the last state + 1

    public RobotLexer(RobotKeywordProvider provider) {
        keywordProvider = provider;
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.position = startOffset;
        this.level = fromState(initialState);
        advance();
    }

    private boolean isSpecial(int position) {
        // special is defined as whitespace or anything we do before checking the state
        return isWhitespace(position) || isNewLine(position) || isComment(position);
    }

    @Override
    public void advance() {
        if (position >= endOffset) {
            currentToken = null;
            return;
        }
        startOffset = position;

        // these are based on the characters of a row at any given time
        if (isComment(this.position)) {
            if (isNewLine(this.position)) {
                currentToken = RobotTokenTypes.WHITESPACE;
                position++;
            } else if (areAtStartOfSuperSpace()) {
                skipWhitespace();
                currentToken = RobotTokenTypes.WHITESPACE;
            } else {
                currentToken = RobotTokenTypes.COMMENT;
                goToEndOfLine();
            }
            return;
        } else if (isNewLine(this.position)) {
            if (!this.level.empty()) {
                int state = this.level.peek();
                if (ARG == state || IMPORT == state) {
                    level.pop();
                    advance();
                    return;
                } else if (KEYWORD == state || SYNTAX == state || VARIABLE_DEFINITION == state) {
                    if (!isEllipsis(this.position)) {
                        level.pop();
                        advance();
                        return;
                    }
                    // else do nothing; keep newline on this level
                } else if (KEYWORD_DEFINITION == state && !isSpecial(position + 1)) {
                    level.pop();
                    advance();
                    return;
                }
            }
            currentToken = RobotTokenTypes.WHITESPACE;
            position++;
            return;
        } else if (isHeading()) {
            goToEndOfLine();
            String line = buffer.subSequence(startOffset, position).toString();
            this.currentToken = RobotTokenTypes.HEADING;
            if (isSettings(line)) {
                this.level.clear();
                this.level.push(SETTINGS_HEADING);
            } else if (isTestCases(line)) {
                this.level.clear();
                this.level.push(TEST_CASES_HEADING);
            } else if (isKeywords(line) || isUserKeywords(line)) {
                // TODO: not sure if keywords and user keywords need to behave differently
                this.level.clear();
                this.level.push(KEYWORDS_HEADING);
            } else if (isVariables(line)) {
                this.level.clear();
                this.level.push(VARIABLES_HEADING);
            } else {
                this.currentToken = RobotTokenTypes.ERROR;
            }
            return;
        }

        // the rest is based on state
        if (level.empty()) {
            // TODO: not sure
            goToEndOfLine();
            this.currentToken = RobotTokenTypes.ERROR;
        } else {
            int state = level.peek();
            if (SETTINGS_HEADING == state) {
                if (areAtStartOfSuperSpace()) {
                    // TODO: error?
                    skipWhitespace();
                }
                goToNextNewLineOrSuperSpace();
                String word = buffer.subSequence(startOffset, position).toString();
                if (isImport(word)) {
                    this.level.push(IMPORT);
                    this.currentToken = RobotTokenTypes.IMPORT;
                } else if (keywordProvider.isGlobalSetting(word)) {
                    this.currentToken = RobotTokenTypes.SETTING;
                    if (keywordProvider.isSyntaxFollowedByKeyword(word)) {
                        this.level.push(SYNTAX);
                    } else if (keywordProvider.isSyntaxFollowedByString(word)) {
                        this.level.push(KEYWORD);
                    } else {
                        goToEndOfLine();
                        this.currentToken = RobotTokenTypes.ERROR;
                    }
                } else {
                    goToEndOfLine();
                    this.currentToken = RobotTokenTypes.ERROR;
                }
            } else if (VARIABLES_HEADING == state) {
                if (areAtStartOfSuperSpace()) {
                    // TODO: error?
                    skipWhitespace();
                }
                goToNextNewLineOrSuperSpace();
                this.level.push(VARIABLE_DEFINITION);
                this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
            } else if (TEST_CASES_HEADING == state || KEYWORDS_HEADING == state) {
                if (areAtStartOfSuperSpace()) {
                    // TODO: error?
                    skipWhitespace();
                }
                goToNextNewLineOrSuperSpace();
                this.level.push(KEYWORD_DEFINITION);
                this.currentToken = RobotTokenTypes.KEYWORD_DEFINITION;
            } else if (KEYWORD_DEFINITION == state) {
                if (areAtStartOfSuperSpace()) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else {
                    skipNonWhitespace();
                    String word = this.buffer.subSequence(this.startOffset, this.position).toString();
                    if (keywordProvider.isSyntaxOfType(RobotTokenTypes.GHERKIN, word)) {
                        currentToken = RobotTokenTypes.GHERKIN;
                        level.push(GHERKIN);
                    } else if (isVariableDeclaration(word)) {
                        currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                        level.push(VARIABLE_DEFINITION);
                    } else {
                        goToNextNewLineOrSuperSpace();
                        word = this.buffer.subSequence(this.startOffset, this.position).toString();
                        if (keywordProvider.isSyntaxOfType(RobotTokenTypes.BRACKET_SETTING, word)) {
                            this.currentToken = RobotTokenTypes.BRACKET_SETTING;
                            if (keywordProvider.isSyntaxFollowedByKeyword(word)) {
                                this.level.push(SYNTAX);
                            } else if (keywordProvider.isSyntaxFollowedByString(word)) {
                                this.level.push(KEYWORD);
                            } else {
                                goToEndOfLine();
                                this.currentToken = RobotTokenTypes.ERROR;
                            }
                        } else {
                            this.currentToken = RobotTokenTypes.KEYWORD;
                            level.push(KEYWORD);
                        }
                    }
                }
            } else if (KEYWORD == state || IMPORT == state || VARIABLE_DEFINITION == state) {
                if (areAtStartOfSuperSpace()) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else if (isEllipsis(this.position)) {
                    if (isOnlyWhitespaceToPreviousLine()) {
                        // if the only thing before the ... is white space then it is the reserved word
                        goToNextNewLineOrSuperSpace();
                        currentToken = RobotTokenTypes.WHITESPACE;
                    } else {
                        // otherwise it is an argument that happens to be ...
                        goToNextNewLineOrSuperSpace();
                        level.push(ARG);
                        this.currentToken = RobotTokenTypes.ARGUMENT;
                    }
                } else {
                    goToNextNewLineOrSuperSpace();
                    level.push(ARG);
                    this.currentToken = RobotTokenTypes.ARGUMENT;
                }
            } else if (SYNTAX == state) {
                if (areAtStartOfSuperSpace()) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else {
                    goToNextNewLineOrSuperSpace();
                    level.push(KEYWORD);
                    this.currentToken = RobotTokenTypes.KEYWORD;
                }
            } else if (ARG == state) {
                level.pop();
                if (!areAtStartOfSuperSpace()) {
                    level.pop();
                }
                advance();
            } else if (GHERKIN == state) {
                level.pop();
                currentToken = RobotTokenTypes.WHITESPACE;
                position++;
            } else {
                throw new RuntimeException("Unknown State: " + state);
            }
        }
    }

    private boolean isVariableDeclaration(String word) {
        return (word.startsWith("${") || word.startsWith("@{")) &&
                (word.endsWith("}") || word.endsWith("}=") || word.endsWith("} ="));
    }

    private boolean isComment(int position) {
        while (position < endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }

        return charAtEquals(position, '#');
    }

    private boolean isNewLine(int position) {
        return charAtEquals(position, '\n');
    }

    private boolean isHeading() {
        return charAtEquals(position, '*') &&
                charAtEquals(position + 1, '*') &&
                charAtEquals(position + 2, '*') &&
                charAtEquals(position + 3, ' ');
    }

    private boolean isEllipsis(int position) {
        while (position < endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }
        return charAtEquals(position, '.') &&
                charAtEquals(position + 1, '.') &&
                charAtEquals(position + 2, '.') &&
                (isWhitespace(position + 3) || isNewLine(position + 3));
    }

    private boolean isOnlyWhitespaceToPreviousLine() {
        int position = this.position - 1;
        while (position >= 0 && !isNewLine(position)) {
            if (!isWhitespace(position)) {
                return false;
            }
            position--;
        }
        return true;
    }

    private boolean isSettings(String line) {
        return "*** Settings ***".equals(line) || "*** Setting ***".equals(line);
    }

    private boolean isTestCases(String line) {
        return "*** Test Cases ***".equals(line) || "*** Test Case ***".equals(line);
    }

    private boolean isKeywords(String line) {
        return "*** Keywords ***".equals(line) || "*** Keyword ***".equals(line);
    }

    private boolean isUserKeywords(String line) {
        return "*** User Keywords ***".equals(line) || "*** User Keyword ***".equals(line);
    }

    private boolean isVariables(String line) {
        return "*** Variables ***".equals(line) || "*** Variable ***".equals(line);
    }

    private boolean isImport(String nextWord) {
        return keywordProvider.isSyntaxOfType(RobotTokenTypes.IMPORT, nextWord);
    }

    private void goToEndOfLine() {
        while (position < endOffset && !isNewLine(this.position)) {
            position++;
        }
    }

    @Override
    public int getState() {
        return toState(this.level);
    }

    public int peekState() {
        return this.level.isEmpty() ? 0 : this.level.peek();
    }

    protected static int toState(Stack<Integer> stack) {
        int value = 0;
        if (!stack.isEmpty()) {
            int rate = 1;
            for (Integer i : stack) {
                value += i * rate;
                rate *= RATE;
            }
        }
        return value;
    }

    protected static Stack<Integer> fromState(int state) {
        Stack<Integer> stack = new Stack<Integer>();
        if (state > 0) {
            while (state > 0) {
                stack.push(state % RATE);
                state /= RATE;
            }
        }
        return stack;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return currentToken;
    }

    @Override
    public int getTokenStart() {
        return startOffset;
    }

    @Override
    public int getTokenEnd() {
        return position;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }

    private void goToNextNewLineOrSuperSpace() {
        while (position < endOffset && !areAtStartOfSuperSpace() && !isNewLine(this.position)) {
            position++;
        }
    }

    private boolean areAtStartOfSuperSpace() {
        return (charAtEquals(position, ' ') && charAtEquals(position + 1, ' '))
                || charAtEquals(position, '\t');
    }

    private void skipNonWhitespace() {
        while (position < endOffset && !isWhitespace(position) && !isNewLine(position)) {
            position++;
        }
    }

    private void skipWhitespace() {
        while (position < endOffset && isWhitespace(position)) {
            position++;
        }
    }

    private boolean charAtEquals(int position, char c) {
        return position < endOffset && buffer.charAt(position) == c;
    }

    private boolean isWhitespace(int position) {
        return position < endOffset && !isNewLine(position) && Character.isWhitespace(buffer.charAt(position));
    }
}
