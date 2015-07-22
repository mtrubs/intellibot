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
    protected static final int SETTINGS = 7;
    protected static final int ARG = 8;
    protected static final int KEYWORD_DEFINITION = 9;
    protected static final int VARIABLE_DEFINITION = 10;
    protected static final int SYNTAX = 11;
    protected static final int GHERKIN = 12;
    protected static final int VARIABLE = 13;
    // TODO: we might run into max int issues at some point
    private static final int RATE = 14; // this should always be the last state + 1

    public RobotLexer(RobotKeywordProvider provider) {
        this.keywordProvider = provider;
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
        if (this.position >= this.endOffset) {
            this.currentToken = null;
            return;
        }
        this.startOffset = this.position;

        // these are based on the characters of a row at any given time
        if (isComment(this.position)) {
            if (isNewLine(this.position)) {
                this.currentToken = RobotTokenTypes.WHITESPACE;
                this.position++;
            } else if (isStartOfSuperSpace(this.position)) {
                skipWhitespace();
                this.currentToken = RobotTokenTypes.WHITESPACE;
            } else {
                this.currentToken = RobotTokenTypes.COMMENT;
                goToEndOfLine();
            }
            return;
        } else if (isNewLine(this.position)) {
            if (!this.level.empty()) {
                int state = this.level.peek();
                if (ARG == state || IMPORT == state) {
                    this.level.pop();
                    advance();
                    return;
                } else if (KEYWORD == state || SYNTAX == state || VARIABLE_DEFINITION == state || SETTINGS == state) {
                    if (!isEllipsis(this.position)) {
                        this.level.pop();
                        advance();
                        return;
                    }
                    // else do nothing; keep newline on this level
                } else if (KEYWORD_DEFINITION == state && !isSpecial(this.position + 1)) {
                    this.level.pop();
                    advance();
                    return;
                }
            }
            this.currentToken = RobotTokenTypes.WHITESPACE;
            this.position++;
            return;
        } else if (isHeading()) {
            goToEndOfLine();
            String line = getCurrentSequence();
            this.currentToken = RobotTokenTypes.HEADING;
            if (isSettings(line)) {
                this.level.clear();
                this.level.push(SETTINGS_HEADING);
            } else if (isTestCases(line)) {
                this.level.clear();
                this.level.push(TEST_CASES_HEADING);
            } else if (isKeywords(line) || isUserKeywords(line)) {
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
        if (this.level.empty()) {
            goToEndOfLine();
            this.currentToken = RobotTokenTypes.ERROR;
        } else {
            int state = this.level.peek();
            if (SETTINGS_HEADING == state) {
                if (isStartOfSuperSpace(this.position)) {
                    skipWhitespace();
                }
                goToNextNewLineOrSuperSpace();
                String word = getCurrentSequence();
                if (isImport(word)) {
                    this.level.push(IMPORT);
                    this.currentToken = RobotTokenTypes.IMPORT;
                } else if (this.keywordProvider.isGlobalSetting(word)) {
                    this.currentToken = RobotTokenTypes.SETTING;
                    if (this.keywordProvider.isSyntaxFollowedByKeyword(word)) {
                        this.level.push(SYNTAX);
                    } else if (this.keywordProvider.isSyntaxFollowedByVariableDefinition(word)) {
                        this.level.push(SETTINGS);
                    } else if (this.keywordProvider.isSyntaxFollowedByString(word)) {
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
                if (isStartOfSuperSpace(this.position)) {
                    skipWhitespace();
                }
                goToNextNewLineOrSuperSpace();
                this.level.push(VARIABLE_DEFINITION);
                this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
            } else if (TEST_CASES_HEADING == state || KEYWORDS_HEADING == state) {
                if (isStartOfSuperSpace(this.position)) {
                    skipWhitespace();
                }
                goToNextNewLineOrSuperSpace();
                this.level.push(KEYWORD_DEFINITION);
                this.currentToken = RobotTokenTypes.KEYWORD_DEFINITION;
            } else if (KEYWORD_DEFINITION == state) {
                if (isStartOfSuperSpace(this.position)) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else {
                    skipNonWhitespace();
                    String word = getCurrentSequence();
                    if (this.keywordProvider.isSyntaxOfType(RobotTokenTypes.GHERKIN, word)) {
                        this.currentToken = RobotTokenTypes.GHERKIN;
                        this.level.push(GHERKIN);
                    } else if (isVariableDeclaration(word)) {
                        goToNextNewLineOrSuperSpace();
                        this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                        this.level.push(VARIABLE_DEFINITION);
                    } else {
                        goToNextNewLineOrSuperSpace();
                        word = getCurrentSequence();
                        if (this.keywordProvider.isSyntaxOfType(RobotTokenTypes.BRACKET_SETTING, word)) {
                            this.currentToken = RobotTokenTypes.BRACKET_SETTING;
                            if (this.keywordProvider.isSyntaxFollowedByKeyword(word)) {
                                this.level.push(SYNTAX);
                            } else if (this.keywordProvider.isSyntaxFollowedByVariableDefinition(word)) {
                                this.level.push(SETTINGS);
                            } else if (this.keywordProvider.isSyntaxFollowedByString(word)) {
                                this.level.push(KEYWORD);
                            } else {
                                goToEndOfLine();
                                this.currentToken = RobotTokenTypes.ERROR;
                            }
                        } else {
                            this.currentToken = RobotTokenTypes.KEYWORD;
                            this.level.push(KEYWORD);
                        }
                    }
                }
            } else if (KEYWORD == state || IMPORT == state || VARIABLE_DEFINITION == state || SETTINGS == state) {
                if (isStartOfSuperSpace(this.position)) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else if (isEllipsis(this.position)) {
                    if (isOnlyWhitespaceToPreviousLine()) {
                        // if the only thing before the ... is white space then it is the reserved word
                        goToNextNewLineOrSuperSpace();
                        this.currentToken = RobotTokenTypes.WHITESPACE;
                    } else {
                        // otherwise it is an argument that happens to be ...
                        goToNextNewLineOrSuperSpace();
                        this.level.push(ARG);
                        this.currentToken = RobotTokenTypes.ARGUMENT;
                    }
                } else {
                    if (VARIABLE_DEFINITION == state && this.level.get(this.level.size() - 2) == KEYWORD_DEFINITION) {
                        // this is a variable assignment inside a keyword definition
                        // next token may be another variable or a keyword
                        goToNextNewLineOrSuperSpace();
                        String word = getCurrentSequence();
                        if (isVariableDeclaration(word)) {
                            this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                        } else {
                            this.level.push(KEYWORD);
                            this.currentToken = RobotTokenTypes.KEYWORD;
                        }
                    } else {
                        goToNextNewLineOrSuperSpaceOrVariable();
                        this.level.push(ARG);
                        if (this.startOffset == this.position) {
                            // if we are in a bracket settings then it is variable definition rather than a variable
                            if (this.level.get(this.level.size() - 2) == SETTINGS) {
                                this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                            } else {
                                this.currentToken = RobotTokenTypes.VARIABLE;
                            }
                            this.level.push(VARIABLE);
                            advance();
                        } else {
                            this.currentToken = RobotTokenTypes.ARGUMENT;
                        }
                    }
                }
            } else if (SYNTAX == state) {
                if (isStartOfSuperSpace(this.position)) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else {
                    goToNextNewLineOrSuperSpace();
                    this.level.push(KEYWORD);
                    this.currentToken = RobotTokenTypes.KEYWORD;
                }
            } else if (ARG == state) {
                if (isStartOfSuperSpace(this.position)) {
                    this.level.pop();
                    advance();
                } else if (isNewLine(this.position)) {
                    this.level.pop();
                    this.level.pop();
                    advance();
                } else {
                    // if it is not a newline or a super space then it is arg text before a variable
                    goToNextNewLineOrSuperSpaceOrVariable();
                    if (this.startOffset == this.position) {
                        this.level.push(VARIABLE);
                        this.currentToken = RobotTokenTypes.VARIABLE;
                        advance();
                    } else {
                        this.currentToken = RobotTokenTypes.ARGUMENT;
                    }
                }
            } else if (VARIABLE == state) {
                goToNextNewLineOrSuperSpaceOrVariableEnd();
                this.level.pop();
            } else if (GHERKIN == state) {
                this.level.pop();
                this.currentToken = RobotTokenTypes.WHITESPACE;
                this.position++;
            } else {
                throw new RuntimeException("Unknown State: " + state);
            }
        }
    }

    private String getCurrentSequence() {
        return this.buffer.subSequence(this.startOffset, this.position).toString();
    }

    private boolean isVariable(int position) {
        // potential start of variable
        if ((charAtEquals(position, '$') || charAtEquals(position, '@')) && charAtEquals(position + 1, '{')) {
            position = position + 2;
            while (position < this.endOffset) {
                if (charAtEquals(position, '}')) {
                    return true;
                }
                if (isStartOfSuperSpace(position)) {
                    return false;
                }
                position++;
            }
        }
        return false;
    }

    private boolean isVariableDeclaration(String word) {
        return (word.startsWith("${") || word.startsWith("@{")) &&
                (word.endsWith("}") || word.endsWith("}=") || word.endsWith("} ="));
    }

    private boolean isComment(int position) {
        while (position < this.endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }

        return charAtEquals(position, '#');
    }

    private boolean isNewLine(int position) {
        return charAtEquals(position, '\n');
    }

    private boolean isHeading() {
        return charAtEquals(this.position, '*') &&
                charAtEquals(this.position + 1, '*') &&
                charAtEquals(this.position + 2, '*') &&
                charAtEquals(this.position + 3, ' ');
    }

    private boolean isEllipsis(int position) {
        while (position < this.endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }
        return charAtEquals(position, '.') &&
                charAtEquals(position + 1, '.') &&
                charAtEquals(position + 2, '.') &&
                (isWhitespace(position + 3) && isWhitespace(position + 4) ||
                        isWhitespace(position + 3) && isNewLine(position + 4) ||
                        isNewLine(position + 3));
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
        return this.keywordProvider.isSyntaxOfType(RobotTokenTypes.IMPORT, nextWord);
    }

    private void goToEndOfLine() {
        while (this.position < this.endOffset && !isNewLine(this.position)) {
            this.position++;
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
        return this.currentToken;
    }

    @Override
    public int getTokenStart() {
        return this.startOffset;
    }

    @Override
    public int getTokenEnd() {
        return this.position;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return this.buffer;
    }

    @Override
    public int getBufferEnd() {
        return this.endOffset;
    }

    private void goToNextNewLineOrSuperSpace() {
        while (this.position < this.endOffset && !isStartOfSuperSpace(this.position) && !isNewLine(this.position)) {
            this.position++;
        }
    }

    private void goToNextNewLineOrSuperSpaceOrVariable() {
        while (this.position < this.endOffset && !isStartOfSuperSpace(this.position) && !isNewLine(this.position) && !isVariable(this.position)) {
            this.position++;
        }
    }

    private void goToNextNewLineOrSuperSpaceOrVariableEnd() {
        while (this.position < this.endOffset && !isStartOfSuperSpace(this.position) && !isNewLine(this.position) && !charAtEquals(this.position, '}')) {
            this.position++;
        }
        if (charAtEquals(this.position, '}')) {
            this.position++;
        }
    }

    private boolean isStartOfSuperSpace(int position) {
        return (charAtEquals(position, ' ') && charAtEquals(position + 1, ' '))
                || charAtEquals(position, '\t');
    }

    private void skipNonWhitespace() {
        while (this.position < this.endOffset && !isWhitespace(this.position) && !isNewLine(this.position)) {
            this.position++;
        }
    }

    private void skipWhitespace() {
        while (this.position < this.endOffset && isWhitespace(this.position)) {
            this.position++;
        }
    }

    private boolean charAtEquals(int position, char c) {
        return position < this.endOffset && this.buffer.charAt(position) == c;
    }

    private boolean isWhitespace(int position) {
        return position < this.endOffset && !isNewLine(position) && Character.isWhitespace(this.buffer.charAt(position));
    }
}
