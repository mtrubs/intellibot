package com.millennialmedia.intellibot.ide.structure;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.RobotBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author mrubino
 * @since 2015-12-15
 */
enum RobotViewElementType {
    File {
        @Nullable
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return element == null ? null : element.getIcon(0);
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.files");
        }
    },
    Heading {
        @Nullable
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return AllIcons.Nodes.Tag;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.headings");
        }
    },
    TestCase {
        @Nullable
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return AllIcons.RunConfigurations.Junit;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.testCases");
        }
    },
    Keyword {
        @Nullable
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return AllIcons.Nodes.Method;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.keywords");
        }
    },
    Variable {
        @Nullable
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return AllIcons.Nodes.Variable;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.variables");
        }
    };

    @Nullable
    protected abstract Icon getIcon(@Nullable PsiElement element);

    @NotNull
    protected abstract String getMessage();
}
