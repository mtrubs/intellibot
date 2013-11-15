package com.millennialmedia.intellibot.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiUtilCore;
import com.millennialmedia.intellibot.psi.impl.HeaderImpl;
import com.millennialmedia.intellibot.psi.impl.KeywordInvokeableImpl;
import com.millennialmedia.intellibot.psi.impl.RobotFileImpl;
import com.millennialmedia.intellibot.psi.impl.SettingKeywordInvokeableImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Stephen Abrams
 */
public class RobotParserDefinition implements ParserDefinition {

    private static final TokenSet WHITESPACE = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(RobotTokenTypes.COMMENT);     // todo SMA are there comments?

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new RobotLexer(new RobotKeywordProvider()); // todo SMA: do we need something as advanced as in GherkinParserDefinition?
    }

    @Override
    public PsiParser createParser(Project project) {
        return new RobotParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return RobotElementTypes.FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITESPACE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        // todo SMA: need scott's definitions

        if (node.getElementType() == RobotElementTypes.KEYWORD_INVOKEABLE) return new KeywordInvokeableImpl(node);
        if (node.getElementType() == RobotElementTypes.SETTING_KEYWORD_INVOKEABLE) return new SettingKeywordInvokeableImpl(node);
        if (node.getElementType() == RobotElementTypes.HEADING) return new HeaderImpl(node);

        return PsiUtilCore.NULL_PSI_ELEMENT;

    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new RobotFileImpl(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        // todo SMA: guessing this is for code cleanup
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
