package com.millennialmedia.intellibot.psi;

import com.intellij.lexer.FlexAdapter;

/**
 * @author mrubino
 * @since 2014-07-01
 */
public class RobotLexer2Adapter extends FlexAdapter {
    public RobotLexer2Adapter() {
        super(new RobotLexer2());
    }
}
