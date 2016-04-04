package com.millennialmedia.intellibot.psi;

import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;

public interface RobotTokenTypes {

    IFileElementType FILE = new IStubFileElementType(RobotLanguage.INSTANCE);
    RobotElementType HEADING = new RobotElementType("HEADING");
    RobotElementType SETTING = new RobotElementType("SETTING");
    RobotElementType BRACKET_SETTING = new RobotElementType("BRACKET_SETTING");
    RobotElementType IMPORT = new RobotElementType("IMPORT");
    RobotElementType KEYWORD_DEFINITION = new RobotElementType("KEYWORD_DEFINITION");
    RobotElementType KEYWORD_DEFINITION_ID = new RobotElementType("KEYWORD_DEFINITION_ID");
    RobotElementType KEYWORD = new RobotElementType("KEYWORD");
    RobotElementType ARGUMENT = new RobotElementType("ARGUMENT");
    RobotElementType VARIABLE_DEFINITION = new RobotElementType("VARIABLE_DEFINITION");
    RobotElementType VARIABLE_DEFINITION_ID = new RobotElementType("VARIABLE_DEFINITION_ID");
    RobotElementType VARIABLE = new RobotElementType("VARIABLE");
    RobotElementType COMMENT = new RobotElementType("COMMENT");
    RobotElementType GHERKIN = new RobotElementType("GHERKIN");
    RobotElementType KEYWORD_STATEMENT = new RobotElementType("KEYWORD_STATEMENT");

    RobotElementType ERROR = new RobotElementType("ERROR");
    RobotElementType WHITESPACE = new RobotElementType("WHITESPACE");
}
