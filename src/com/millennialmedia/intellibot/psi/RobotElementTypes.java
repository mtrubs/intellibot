package com.millennialmedia.intellibot.psi;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;

/**
 * @author Stephen Abrams
 */
public interface RobotElementTypes {
    RobotLanguage LANG = RobotLanguage.INSTANCE;

    IFileElementType FILE = new IStubFileElementType(LANG);

    IElementType KEYWORD_INVOKEABLE = new RobotElementType("keyword_invokable");
    IElementType KEYWORD_DEFINTION = new RobotElementType("keyword_definition");
    IElementType SETTING_KEYWORD_INVOKEABLE = new RobotElementType("setting_keyword_invokable");
    IElementType HEADING = new RobotElementType("heading");
    IElementType ARGUEMENT = new RobotElementType("arguement");
//    IStubElementType PROPERTY = new PropertyStubElementType();
//
//    IStubElementType PROPERTIES_LIST = new PropertyListStubElementType();
//    TokenSet PROPERTIES = TokenSet.create(PROPERTY);
}
