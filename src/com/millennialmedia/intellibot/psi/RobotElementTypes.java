package com.millennialmedia.intellibot.psi;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;

/**
 * @author: Stephen Abrams
 */
public interface RobotElementTypes {
    RobotLanguage LANG = RobotLanguage.INSTANCE;

    IFileElementType FILE = new IStubFileElementType(LANG);
//    IStubElementType PROPERTY = new PropertyStubElementType();
//
//    IStubElementType PROPERTIES_LIST = new PropertyListStubElementType();
//    TokenSet PROPERTIES = TokenSet.create(PROPERTY);
}
