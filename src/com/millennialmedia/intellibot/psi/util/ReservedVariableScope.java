package com.millennialmedia.intellibot.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.psi.ref.PythonResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 * @since 2016-03-29
 */
public enum ReservedVariableScope {

    Global,
    TestCase,
    KeywordTeardown,
    TestTeardown,
    SuiteTeardown;

    @Nullable
    public PsiElement getVariable(@NotNull Project project) {
        return PythonResolver.findVariable("GLOBAL_VARIABLES", project);
    }
}
