package com.millennialmedia.intellibot.psi;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class RobotLexer extends LexerBase {

    protected static final int NONE = 0;
    protected static final int SETTINGS_HEADING = 1;
    protected static final int TEST_CASES_HEADING = 2;
    protected static final int KEYWORDS_HEADING = 3;
    protected static final int VARIABLES_HEADING = 4;
    protected static final int IMPORT = 5;
    protected static final int KEYWORD = 6;
    protected static final int SETTINGS = 7;
    protected static final int KEYWORD_DEFINITION = 8;
    protected static final int VARIABLE_DEFINITION = 9;
    protected static final int SYNTAX = 10;
    protected static final int GHERKIN = 11;
    // we might run into max int issues at some point
    private static final int RATE = 12; // this should always be the last state + 1
    private final RobotKeywordProvider keywordProvider;
    private CharSequence buffer = ArrayUtil.EMPTY_CHAR_SEQUENCE;
    private int startOffset;
    private int endOffset;
    private int position;
    private IElementType currentToken;
    private Stack<Integer> level = new Stack<Integer>();

    public RobotLexer(RobotKeywordProvider provider) {
        this.keywordProvider = provider;
    }

    private static boolean isSettings(String line) {
        return "*** Settings ***".equals(line) || "*** Setting ***".equals(line);
    }

    private static boolean isTestCases(String line) {
        return "*** Test Cases ***".equals(line) || "*** Test Case ***".equals(line);
    }

    private static boolean isKeywords(String line) {
        return "*** Keywords ***".equals(line) || "*** Keyword ***".equals(line);
    }

    private static boolean isUserKeywords(String line) {
        return "*** User Keywords ***".equals(line) || "*** User Keyword ***".equals(line);
    }

    private static boolean isVariables(String line) {
        return "*** Variables ***".equals(line) || "*** Variable ***".equals(line);
    }

    protected static int toState(List<Integer> stack) {
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
        int state = peekState();
        int parentState = this.level.size() > 1 ? this.level.get(this.level.size() - 2) : NONE;

        // these are based on the characters of a row at any given time
        if (isComment(this.position)) {
            if (isNewLine(this.position)) {
                this.currentToken = RobotTokenTypes.WHITESPACE;
                this.position++;
            } else if (isSuperSpace(this.position)) {
                skipWhitespace();
                this.currentToken = RobotTokenTypes.WHITESPACE;
            } else {
                this.currentToken = RobotTokenTypes.COMMENT;
                goToEndOfLine();
            }
            return;
        } else if (isNewLine(this.position)) {
            if (KEYWORD == state || IMPORT == state || SYNTAX == state ||
                    VARIABLE_DEFINITION == state || SETTINGS == state) {
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
            this.currentToken = RobotTokenTypes.WHITESPACE;
            this.position++;
            return;
        } else if (isHeading(this.position)) {
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
        if (NONE == state) {
            // this is the instance where no '*** setting ***' has been detected yet.
            goToEndOfLine();
            this.currentToken = RobotTokenTypes.COMMENT;
        } else {
            if (SETTINGS_HEADING == state) {
                if (isSuperSpace(this.position)) {
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
                        this.level.push(IMPORT);
                    } else {
                        goToEndOfLine();
                        this.currentToken = RobotTokenTypes.ERROR;
                    }
                } else {
                    goToEndOfLine();
                    this.currentToken = RobotTokenTypes.ERROR;
                }
            } else if (VARIABLES_HEADING == state) {
                if (isSuperSpace(this.position)) {
                    skipWhitespace();
                }
                if (isVariable(this.position)) {
                    goToVariableEnd();
                    //goToNextNewLineOrSuperSpace();
                    this.level.push(VARIABLE_DEFINITION);
                    this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                } else {
                    goToEndOfLine();
                    this.currentToken = RobotTokenTypes.ERROR;
                }
            } else if (TEST_CASES_HEADING == state || KEYWORDS_HEADING == state) {
                if (isSuperSpace(this.position)) {
                    skipWhitespace();
                }
                if (isVariable(this.position)) {
                    goToVariableEnd();
                    this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                } else {
                    goToNextNewLineOrSuperSpaceOrVariable();
                    this.currentToken = RobotTokenTypes.KEYWORD_DEFINITION;
                }
                if (isSuperSpaceOrNewline(this.position)) {
                    this.level.push(KEYWORD_DEFINITION);
                }
            } else if (KEYWORD_DEFINITION == state) {
                if (isSuperSpace(this.position)) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else if (lookAheadForLoop()) {
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else if (isVariable(this.position)) {
                    goToVariableEnd();
                    if (isVariableDefinition(this.position)) {
//                        goToNextNewLineOrSuperSpace();
                        this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                        this.level.push(VARIABLE_DEFINITION);
                    } else {
                        this.currentToken = RobotTokenTypes.VARIABLE;
                        this.level.push(KEYWORD);
                        if (!isSuperSpaceOrNewline(this.position)) {
                            this.level.push(KEYWORD);
                        }
                    }
                } else {
                    skipNonWhitespace();
                    String word = getCurrentSequence();
                    if (this.keywordProvider.isSyntaxOfType(RobotTokenTypes.GHERKIN, word)) {
                        this.currentToken = RobotTokenTypes.GHERKIN;
                        this.level.push(GHERKIN);
                    } else {
                        goToNextNewLineOrSuperSpaceOrVariable();
                        word = getCurrentSequence();
                        if (this.keywordProvider.isSyntaxOfType(RobotTokenTypes.BRACKET_SETTING, word)) {
                            this.currentToken = RobotTokenTypes.BRACKET_SETTING;
                            if (this.keywordProvider.isSyntaxFollowedByKeyword(word)) {
                                this.level.push(SYNTAX);
                            } else if (this.keywordProvider.isSyntaxFollowedByVariableDefinition(word)) {
                                this.level.push(SETTINGS);
                            } else if (this.keywordProvider.isSyntaxFollowedByString(word)) {
                                this.level.push(IMPORT);
                            } else {
                                goToEndOfLine();
                                this.currentToken = RobotTokenTypes.ERROR;
                            }
                        } else {
                            this.currentToken = RobotTokenTypes.KEYWORD;
                            this.level.push(KEYWORD);
                            if (!isSuperSpaceOrNewline(this.position)) {
                                this.level.push(KEYWORD);
                            }
                        }
                    }
                }
            } else if (KEYWORD == state || IMPORT == state || VARIABLE_DEFINITION == state || SETTINGS == state) {
                if (isSuperSpace(this.position)) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                    if (KEYWORD == state && KEYWORD == parentState) {
                        this.level.pop();
                    }
                } else if (isEllipsis(this.position)) {
                    if (isOnlyWhitespaceToPreviousLine(this.position - 1)) {
                        // if the only thing before the ... is white space then it is the reserved word
                        goToNextNewLineOrSuperSpace();
                        this.currentToken = RobotTokenTypes.WHITESPACE;
                    } else {
                        // otherwise it is an argument that happens to be ...
                        goToNextNewLineOrSuperSpace();
                        this.currentToken = RobotTokenTypes.ARGUMENT;
                    }
                } else if (VARIABLE_DEFINITION == state && isVariableEnd(this.position - 1)) {
                    goToNextNewLineOrSuperSpace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else {
                    if (VARIABLE_DEFINITION == state && KEYWORD_DEFINITION == parentState) {
                        // this is a variable assignment inside a keyword definition: "${var} =  some keyword  arg1  arg2"
                        // next token may be another variable or a keyword
                        if (isVariable(this.position)) {
                            goToVariableEnd();
//                            if (isVariableDefinition(this.position)) {
//                                goToNextNewLineOrSuperSpace();
//                            }
                            this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                        } else {
                            goToNextNewLineOrSuperSpace();
                            this.level.push(KEYWORD);
                            this.currentToken = RobotTokenTypes.KEYWORD;
                        }
                    } else if (isVariable(this.position)) {
                        goToVariableEnd();
                        // if we are in a bracket settings then it is variable definition rather than a variable
                        if (SETTINGS == state && isSuperSpacePrevious()) {
                            this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                        } else {
                            this.currentToken = RobotTokenTypes.VARIABLE;
                        }
                    } else if (KEYWORD == state && KEYWORD == parentState) {
                        goToNextNewLineOrSuperSpaceOrVariable();
                        this.currentToken = RobotTokenTypes.KEYWORD;
                    } else {
                        goToNextNewLineOrSuperSpaceOrVariable();
                        this.currentToken = RobotTokenTypes.ARGUMENT;
                    }
                }
            } else if (SYNTAX == state) {
                if (isSuperSpace(this.position)) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else {
                    goToNextNewLineOrSuperSpace();
                    this.level.push(KEYWORD);
                    this.currentToken = RobotTokenTypes.KEYWORD;
                }
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
        if (isVariableStart(position)) {
            position += 2;
            if (isNumber(position))
                return false;
            int count = 1;
            while (count > 0 && position < this.endOffset && position >= 0) {
                if (isVariableEnd(position)) {
                    count--;
                    if (count == 0) {
                        return true;
                    }
                }
                if (isVariableStart(position)) {
                    count++;
                    position += 2;
                }
                if (isSuperSpaceOrNewline(position)) {
                    return false;
                }
                position++;
            }
        }
        return false;
    }

    private boolean isVariableDefinition(int position) {
        return isSuperSpaceOrNewline(position) ||
                charAtEquals(position, '=') && isSuperSpaceOrNewline(position + 1) ||
                isSpace(position) && charAtEquals(position + 1, '=') && isSuperSpaceOrNewline(position + 2);
    }

    private boolean isComment(int position) {
        while (position < this.endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }

        return charAtEquals(position, '#');
    }

    private boolean isVariableStart(int position) {
        return (charAtEquals(position, '$') ||
                charAtEquals(position, '@') ||
                charAtEquals(position, '%') ||
                charAtEquals(position, '&')) && charAtEquals(position + 1, '{');
    }

    private boolean isVariableEnd(int position) {
        return charAtEquals(position, '}');
    }

    private boolean isNewLine(int position) {
        return charAtEquals(position, '\n');
    }

    private boolean isSpace(int position) {
        return charAtEquals(position, ' ');
    }

    private boolean isTab(int position) {
        return charAtEquals(position, '\t');
    }

    private boolean isHeading(int position) {
        return charAtEquals(position, '*') &&
                charAtEquals(position + 1, '*') &&
                charAtEquals(position + 2, '*') &&
                isSpace(position + 3);
    }

    private boolean isEllipsis(int position) {
        while (position < this.endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }
        return charAtEquals(position, '.') &&
                charAtEquals(position + 1, '.') &&
                charAtEquals(position + 2, '.') &&
                isSuperSpaceOrNewline(position + 3);
    }

    private boolean isOnlyWhitespaceToPreviousLine(int position) {
        // TODO: "..." in FOR loop body that begin with "\"
        while (position >= 0 && !isNewLine(position)) {
            if (!isWhitespace(position)) {
                return false;
            }
            position--;
        }
        return true;
    }

    private boolean isSuperSpacePrevious() {
        int position = this.startOffset - 1;
        while (position >= 0 && !isWhitespace(position)) {
            if (!isWhitespace(position)) {
                return false;
            }
            position--;
        }
        return true;
    }

    private boolean isSuperSpace(int position) {
        return isSpace(position) && isSpace(position + 1) ||
                isSpace(position) && isTab(position + 1) ||
                isTab(position);
    }

    private boolean isSuperSpaceOrNewline(int position) {
        return isSuperSpace(position) || isNewLine(position);
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
        return this.level.isEmpty() ? NONE : this.level.peek();
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
        while (this.position < this.endOffset && !isSuperSpaceOrNewline(this.position)) {
            this.position++;
        }
    }

    private void goToNextNewLineOrSuperSpaceOrVariable() {
        while (this.position < this.endOffset && !isSuperSpaceOrNewline(this.position) && !isVariable(this.position)) {
            this.position++;
        }
    }

    private void goToVariableEnd() {
        // TODO: make better for nested variables
        // this only works currently because it is always wrapped in an isVariable(position) call
        // so we can safely assume that we are at the beginning of a balanced set of { and }
        int count = 0;
        if (isVariableStart(this.position)) {
            count++;
            this.position++;
        }
        while (this.position < this.endOffset && count > 0) {
            if (isVariableStart(this.position)) {
                count++;
            } else if (isVariableEnd(this.position)) {
                count--;
            }
            this.position++;
        }
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

    private boolean isNumber(int position) {
        return position < this.endOffset && (Character.isDigit(this.buffer.charAt(position)) || this.buffer.charAt(position) == '-');
    }

    private static final Pattern FOR_PATTERN = Pattern.compile("(?:: ?)?FOR|\\\\|END");
    private boolean lookAheadForLoop() {
        int p = this.position;
        goToNextNewLineOrSuperSpace();
        if (FOR_PATTERN.matcher(this.buffer.subSequence(this.startOffset, this.position)).matches()) {
            return true;
        } else {
            this.position = p;
            return false;
        }
    }
}
