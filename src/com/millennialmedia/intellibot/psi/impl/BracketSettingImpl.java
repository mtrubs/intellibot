/* Jumptap's products may be patented under one or more of the patents listed at www.jumptap.com/intellectual-property */
package com.millennialmedia.intellibot.psi.impl;

import com.intellij.lang.ASTNode;
import com.millennialmedia.intellibot.psi.KeywordInvokeable;
import com.millennialmedia.intellibot.psi.RobotPsiElementBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class BracketSettingImpl extends RobotPsiElementBase implements KeywordInvokeable {

    public BracketSettingImpl(@NotNull final ASTNode node) {
        super(node);
    }
}