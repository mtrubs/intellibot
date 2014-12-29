package com.millennialmedia.intellibot.psi.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.millennialmedia.intellibot.psi.RobotTokenTypes;
import static com.millennialmedia.intellibot.psi.RobotTokenTypes.*;

%%

%public
%class RobotLexer2
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
return;
%eof}

CRLF= \n|\r|\r\n

%state WAITING_VALUE

%%

<YYINITIAL>
    {CRLF}      { yybegin(YYINITIAL); return RobotTokenTypes.WHITESPACE; }

.                                                           { return RobotTokenTypes.SETTING; }