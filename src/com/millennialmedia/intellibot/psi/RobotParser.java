package com.millennialmedia.intellibot.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Stephen Abrams
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

    //    TC_KW_NAME todo this is definition of a new keyword
    private static void parseFileTopLevel(PsiBuilder builder) {
        while (!builder.eof()) {

            final IElementType tokenType = builder.getTokenType();

            // todo SMA
            if (tokenType == RobotTokenTypes.HEADING) {
                final PsiBuilder.Marker marker = builder.mark();
                assert builder.getTokenType() == RobotTokenTypes.HEADING;
                marker.done(RobotElementTypes.HEADING);
                while (true) {
                    builder.advanceLexer();
                    if (builder.getTokenType() == RobotTokenTypes.SETTING)
                        parseSetting(builder);
                    if (builder.getTokenType() == RobotTokenTypes.KEYWORD)
                        parseKeyword(builder);
                    if (builder.getTokenType() == RobotTokenTypes.TC_KW_NAME)
                        parseKeywordDefinition(builder);
                    if (builder.eof()) break;
                }
            } else {
                builder.advanceLexer();
            }
        }
    }

    private static void parseKeywordDefinition(PsiBuilder builder) {
        assert builder.getTokenType() == RobotTokenTypes.TC_KW_NAME;
        final PsiBuilder.Marker keywordDefMarker;
        keywordDefMarker = builder.mark();

        keywordDefMarker.done(RobotElementTypes.KEYWORD_DEFINTION);
    }

    private static void parseSetting(PsiBuilder builder) {
        assert builder.getTokenType() == RobotTokenTypes.SETTING;
        final PsiBuilder.Marker keywordMarker;
        keywordMarker = builder.mark();
        parseArguements(builder);
        keywordMarker.done(RobotElementTypes.SETTING_KEYWORD_INVOKEABLE);
    }

    private static void parseKeyword(PsiBuilder builder) {
        assert builder.getTokenType() == RobotTokenTypes.KEYWORD;
        final PsiBuilder.Marker keywordMarker;
        keywordMarker = builder.mark();
        parseArguements(builder);
        keywordMarker.done(RobotElementTypes.KEYWORD_INVOKEABLE);

    }

    private static void parseArgument(PsiBuilder builder) {
        final PsiBuilder.Marker argumentMarker = builder.mark();
        assert builder.getTokenType() == RobotTokenTypes.ARGUMENT;
        argumentMarker.done(RobotElementTypes.ARGUEMENT);
    }

    private static void parseArguements(PsiBuilder builder) {
        while (true) {
            builder.advanceLexer();
            if (builder.getTokenType() == RobotTokenTypes.ARGUMENT) {
                parseArgument(builder);
            } else {
                break;
            }
        }
    }

}

