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
        marker.done(RobotElementTypes.FILE);
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
                if (RobotTokenTypes.IMPORT == type) {
                    parseImport(builder);
                } else if (RobotTokenTypes.SETTING == type) {
                    parseSetting(builder);
                } else if (RobotTokenTypes.KEYWORD_DEFINITION == type) {
                    parseKeywordDefinition(builder);
                } else {
                    // TODO: other types
                    System.out.println(type);
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
                if (keywordMarker != null) {
                    keywordMarker.done(RobotTokenTypes.KEYWORD_DEFINITION);
                }
                keywordMarker = builder.mark();
                builder.advanceLexer();
            }

            if (builder.eof()) {
                if (keywordMarker != null) {
                    keywordMarker.done(RobotTokenTypes.KEYWORD_DEFINITION);
                }
                break;
            } else {
                // TODO: other types
                type = builder.getTokenType();
                if (RobotTokenTypes.HEADING == type) {
                    if (keywordMarker != null) {
                        keywordMarker.done(RobotTokenTypes.KEYWORD_DEFINITION);
                    }
                    break;
                } else if (RobotTokenTypes.BRACKET_SETTING == type) {
                    parseBracketSetting(builder);
                } else {
                    System.out.println(type);
                    builder.advanceLexer();
                }
            }
        }
    }

    private static void parseBracketSetting(PsiBuilder builder) {
        parseWithArguments(builder, RobotTokenTypes.BRACKET_SETTING);
    }

    private static void parseImport(PsiBuilder builder) {
        parseWithArguments(builder, RobotTokenTypes.IMPORT);
    }

    private static void parseSetting(PsiBuilder builder) {
        parseWithArguments(builder, RobotTokenTypes.SETTING);
    }

    private static void parseWithArguments(PsiBuilder builder, RobotElementType markType) {
        IElementType type = builder.getTokenType();
        assert markType == type;
        PsiBuilder.Marker importMarker = builder.mark();
        do {
            builder.advanceLexer();
            if (builder.eof()) {
                break;
            }
            type = builder.getTokenType();
            if (RobotTokenTypes.ARGUMENT == type) {
                parseArgument(builder);
            }
        } while (RobotTokenTypes.ARGUMENT == type);
        importMarker.done(markType);
    }

    private static void parseArgument(PsiBuilder builder) {
        PsiBuilder.Marker argumentMarker = builder.mark();
        assert builder.getTokenType() == RobotTokenTypes.ARGUMENT;
        argumentMarker.done(RobotTokenTypes.ARGUMENT);
    }
}

