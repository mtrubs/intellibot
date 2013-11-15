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

    private static void parseFileTopLevel(PsiBuilder builder) {
//        while(!builder.eof()) {
//            final IElementType tokenType = builder.getTokenType();
//
//            // todo SMA
//            if (tokenType == RobotTokenTypes.ARGUMENT) {
////                parseFeature(builder);
////            } else if (tokenType == GherkinTokenTypes.TAG) {
////                parseTags(builder);
////            } else {
////                builder.advanceLexer();
//            }
//        }
    }
}
