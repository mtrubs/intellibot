package com.millennialmedia.intellibot.psi;

import com.intellij.psi.tree.TokenSet;

public interface RobotTokenTypes {

    RobotElementType HEADING = new RobotElementType("HEADING");
    RobotElementType SETTING = new RobotElementType("SETTING");
    RobotElementType BRACKET_SETTING = new RobotElementType("BRACKET_SETTING");
    RobotElementType IMPORT = new RobotElementType("IMPORT");
    RobotElementType KEYWORD_DEFINITION = new RobotElementType("KEYWORD_DEFINITION");
    RobotElementType KEYWORD = new RobotElementType("KEYWORD");
    RobotElementType ARGUMENT = new RobotElementType("ARGUMENT");
    RobotElementType VARIABLE = new RobotElementType("VARIABLE");
    RobotElementType COMMENT = new RobotElementType("COMMENT");
    RobotElementType SYNTAX = new RobotElementType("SYNTAX");
    RobotElementType GHERKIN = new RobotElementType("GHERKIN");

    RobotElementType SEPARATOR = new RobotElementType("SEPARATOR");
    RobotElementType ERROR = new RobotElementType("ERROR");
    RobotElementType WHITESPACE = new RobotElementType("WHITESPACE");

    TokenSet KEYWORDS = TokenSet.create(HEADING, SETTING, BRACKET_SETTING, IMPORT,
            KEYWORD_DEFINITION, KEYWORD, ARGUMENT, VARIABLE, COMMENT, SYNTAX, GHERKIN);
}
