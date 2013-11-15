/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class RobotLanguage extends Language {

    public static RobotLanguage INSTANCE = new RobotLanguage();


    protected RobotLanguage() {
        super("Robot", "");
    }
//    protected RobotLanguage() {
//        super("Properties");  //, "text/properties"
//        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(this, new SingleLazyInstanceSyntaxHighlighterFactory() {
//            @NotNull
//            protected SyntaxHighlighter createHighlighter() {
//                return new RobotHighlighter();
//            }
//        });
//    }

    @Override
    public String getDisplayName() {
        return "Robot";
    }
}
