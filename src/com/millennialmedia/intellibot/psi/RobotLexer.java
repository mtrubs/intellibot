package com.millennialmedia.intellibot.psi;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

/**
 * @author: Stephen Abrams
 */
public class RobotLexer extends Lexer {
    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getState() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getTokenStart() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getTokenEnd() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void advance() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LexerPosition getCurrentPosition() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void restore(LexerPosition position) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CharSequence getBufferSequence() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getBufferEnd() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
