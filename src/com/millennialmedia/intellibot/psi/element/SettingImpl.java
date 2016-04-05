package com.millennialmedia.intellibot.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author mrubino
 */
public class SettingImpl extends RobotPsiElementBase implements Setting {

    private static final String SUITE_TEARDOWN = "Suite Teardown";
    private static final String TEST_TEARDOWN = "Test Teardown";

    public SettingImpl(@NotNull final ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSuiteTeardown() {
        // TODO: better OO
        return SUITE_TEARDOWN.equalsIgnoreCase(getPresentableText());
    }

    @Override
    public boolean isTestTeardown() {
        // TODO: better OO
        return TEST_TEARDOWN.equalsIgnoreCase(getPresentableText());
    }
}
