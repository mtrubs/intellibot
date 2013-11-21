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

    private static String getSafeTokenText(PsiBuilder builder) {
        String tokenText = builder.getTokenText();
        return tokenText == null ? "" : tokenText.trim();
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
        PsiBuilder.Marker headingMarker = null;
        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (RobotTokenTypes.HEADING == type) {
                if (headingMarker != null) {
                    headingMarker.done(RobotTokenTypes.HEADING);
                }
                headingMarker = builder.mark();
            }

            builder.advanceLexer();
            if (!builder.eof()) {
                if (RobotTokenTypes.IMPORT == type) {
                    parseImport(builder);
                } else if (RobotTokenTypes.SETTING == type) {
                    parseSetting(builder);
                } else {
                    System.out.println(builder.getTokenType());
                }
            } else if (headingMarker != null) {
                headingMarker.done(RobotTokenTypes.HEADING);
            }
        }
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
            if (builder.eof()) {
                break;
            }
            builder.advanceLexer();
            type = builder.getTokenType();
            if (RobotTokenTypes.ARGUMENT == type) {
                parseArgument(builder);
            }
        } while (RobotTokenTypes.ARGUMENT == type);
        importMarker.done(markType);
    }


    private static void parseKeywordDefinition(PsiBuilder builder) {
        assert builder.getTokenType() == RobotTokenTypes.KEYWORD_DEFINITION;
        final PsiBuilder.Marker keywordDefMarker;
        keywordDefMarker = builder.mark();

        keywordDefMarker.done(RobotElementTypes.KEYWORD_DEFINTION);
    }

    private static void parseKeyword(PsiBuilder builder) {
        assert builder.getTokenType() == RobotTokenTypes.KEYWORD;
        final PsiBuilder.Marker keywordMarker;
        keywordMarker = builder.mark();
        parseArguements(builder);
        keywordMarker.done(RobotElementTypes.KEYWORD_INVOKEABLE);

    }

    private static void parseArgument(PsiBuilder builder) {
        PsiBuilder.Marker argumentMarker = builder.mark();
        assert builder.getTokenType() == RobotTokenTypes.ARGUMENT;
        argumentMarker.done(RobotTokenTypes.ARGUMENT);
    }

    private static void parseArguements(PsiBuilder builder) {
        while (true) {
            builder.advanceLexer();
            if (builder.getTokenType() == RobotTokenTypes.ARGUMENT) {
                parseArgument(builder);
            } else if (builder.getTokenType() != RobotTokenTypes.WHITESPACE) {
                break;
            }
        }
    }

}

