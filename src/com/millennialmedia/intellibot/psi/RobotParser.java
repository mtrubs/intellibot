package com.millennialmedia.intellibot.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stephen Abrams
 */
public class RobotParser implements PsiParser {

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {

        final PsiBuilder.Marker marker = builder.mark();
        parseFileTopLevel(builder);
        marker.done(RobotTokenTypes.FILE);
        return builder.getTreeBuilt();

    }

    private static void parseFileTopLevel(PsiBuilder builder) {
        while (!builder.eof()) {
            IElementType tokenType = builder.getTokenType();
            if (RobotTokenTypes.HEADING == tokenType) {
                parseHeading(builder);
            } else {
                builder.advanceLexer();
            }
        }
    }

    private static void parseHeading(PsiBuilder builder) {
        assert RobotTokenTypes.HEADING == builder.getTokenType();
        PsiBuilder.Marker headingMarker = null;
        while (true) {
            IElementType type = builder.getTokenType();
            if (RobotTokenTypes.HEADING == type) {
                if (headingMarker != null) {
                    headingMarker.done(RobotTokenTypes.HEADING);
                }
                headingMarker = builder.mark();
                builder.advanceLexer();
            }

            if (builder.eof()) {
                if (headingMarker != null) {
                    headingMarker.done(RobotTokenTypes.HEADING);
                }
                break;
            } else {
                type = builder.getTokenType();
                if (RobotTokenTypes.HEADING == type) {
                    continue;
                } else if (RobotTokenTypes.IMPORT == type) {
                    parseImport(builder);
                } else if (RobotTokenTypes.VARIABLE_DEFINITION == type) {
                    parseVariableDefinition(builder);
                } else if (RobotTokenTypes.SETTING == type) {
                    parseSetting(builder);
                } else if (RobotTokenTypes.KEYWORD_DEFINITION == type) {
                    parseKeywordDefinition(builder);
                } else if (RobotTokenTypes.KEYWORD == type) {
                    parseKeywordStatement(builder, RobotTokenTypes.KEYWORD_STATEMENT, false);
                } else {
                    // TODO: other types; error
                    //System.out.println(type);
                    builder.advanceLexer();
                }
            }
        }
    }

    private static void parseKeywordDefinition(PsiBuilder builder) {
        assert RobotTokenTypes.KEYWORD_DEFINITION == builder.getTokenType();

        PsiBuilder.Marker keywordMarker = null;
        while (true) {
            IElementType type = builder.getTokenType();
            if (RobotTokenTypes.KEYWORD_DEFINITION == type) {
                if (builder.rawLookup(-1) != RobotTokenTypes.VARIABLE_DEFINITION) {
                    if (keywordMarker != null) {
                        keywordMarker.done(RobotTokenTypes.KEYWORD_DEFINITION);
                    }
                    keywordMarker = builder.mark();
                }
                builder.advanceLexer();
            }

            if (builder.eof()) {
                if (keywordMarker != null) {
                    keywordMarker.done(RobotTokenTypes.KEYWORD_DEFINITION);
                }
                break;
            } else {
                type = builder.getTokenType();
                if (RobotTokenTypes.HEADING == type) {
                    if (keywordMarker != null) {
                        keywordMarker.done(RobotTokenTypes.KEYWORD_DEFINITION);
                    }
                    break;
                } else if (RobotTokenTypes.BRACKET_SETTING == type) {
                    parseBracketSetting(builder);
                } else if (RobotTokenTypes.ERROR == type) {
                    // TODO: not sure
                    builder.advanceLexer();
                } else if (RobotTokenTypes.VARIABLE_DEFINITION == type) {
                    parseKeywordStatement(builder, RobotTokenTypes.VARIABLE_DEFINITION, true);
                } else {
                    parseKeywordStatement(builder, RobotTokenTypes.KEYWORD_STATEMENT, false);
                }
            }
        }
    }

    private static void parseKeywordStatement(PsiBuilder builder, IElementType rootType, boolean skipGherkin) {
        PsiBuilder.Marker keywordStatementMarker = builder.mark();
        boolean seenGherkin = skipGherkin;
        boolean seenKeyword = false;
        while (true) {
            if (builder.eof()) {
                break;
            }
            IElementType type = builder.getTokenType();
            if (type == RobotTokenTypes.GHERKIN) {
                // if we see a keyword or variable there should be no Gherkin unless we are on a new statement
                if (seenGherkin || seenKeyword) {
                    break;
                } else {
                    seenGherkin = true;
                    // nothing to do for this
                    builder.advanceLexer();
                }
            } else if (type == RobotTokenTypes.KEYWORD) {
                if (seenKeyword) {
                    break;
                } else {
                    seenKeyword = true;
                    parseSimple(builder, RobotTokenTypes.KEYWORD);
                }
            } else if (type == RobotTokenTypes.ARGUMENT || type == RobotTokenTypes.VARIABLE) {
                parseArgWithVars(builder, type, RobotTokenTypes.VARIABLE);
            } else if (type == RobotTokenTypes.VARIABLE_DEFINITION) {
                if (seenKeyword) {
                    break;
                } else {
                    seenKeyword = true;
                    builder.advanceLexer();
                    if (builder.getTokenType() == RobotTokenTypes.KEYWORD) {
                        parseKeywordStatement(builder, RobotTokenTypes.KEYWORD_STATEMENT, true);
                    }
                }
            } else {
                // TODO: other types; error?
                //System.out.println(type);
                break;
            }
        }

        keywordStatementMarker.done(rootType);
    }

    private static void parseBracketSetting(PsiBuilder builder) {
        parseWithArguments(builder, RobotTokenTypes.BRACKET_SETTING, RobotTokenTypes.VARIABLE_DEFINITION);
    }

    private static void parseImport(PsiBuilder builder) {
        parseWithArguments(builder, RobotTokenTypes.IMPORT, RobotTokenTypes.VARIABLE);
    }

    private static void parseVariableDefinition(PsiBuilder builder) {
        parseWithArguments(builder, RobotTokenTypes.VARIABLE_DEFINITION, RobotTokenTypes.VARIABLE);
    }

    private static void parseSetting(PsiBuilder builder) {
        parseWithArguments(builder, RobotTokenTypes.SETTING, null);
    }

    private static void parseWithArguments(PsiBuilder builder, IElementType markType, IElementType subType) {
        IElementType type = builder.getTokenType();
        assert markType == type;
        PsiBuilder.Marker importMarker = builder.mark();
        builder.advanceLexer();
        while (true) {
            if (builder.eof()) {
                break;
            }
            type = builder.getTokenType();
            if (RobotTokenTypes.ARGUMENT == type || RobotTokenTypes.VARIABLE == type) {
                parseArgWithVars(builder, RobotTokenTypes.ARGUMENT, subType);
            } else if (markType != RobotTokenTypes.VARIABLE_DEFINITION && RobotTokenTypes.VARIABLE_DEFINITION == type) {
                if (builder.rawLookup(-1) == RobotTokenTypes.WHITESPACE && builder.rawLookup(-2) == RobotTokenTypes.WHITESPACE) {
                    break;
                }
                parseVariableDefinition(builder);

            } else {
                break;
            }
        }
        importMarker.done(markType);
    }

    private static void parseArgWithVars(PsiBuilder builder, IElementType type, IElementType subType) {
        PsiBuilder.Marker arg = builder.mark();
        while (type == RobotTokenTypes.ARGUMENT || type == subType) {
            boolean end = builder.rawLookup(1) == RobotTokenTypes.WHITESPACE;
            if (type == subType) {
                parseSimple(builder, type);
            } else {
                builder.advanceLexer();
            }
            if (end) {
                break;
            }
            type = builder.getTokenType();
        }
        arg.done(RobotTokenTypes.ARGUMENT);
    }

    private static void parseSimple(PsiBuilder builder, IElementType type) {
        assert builder.getTokenType() == type;
        PsiBuilder.Marker argumentMarker = builder.mark();
        builder.advanceLexer();
        argumentMarker.done(type);
    }
}

