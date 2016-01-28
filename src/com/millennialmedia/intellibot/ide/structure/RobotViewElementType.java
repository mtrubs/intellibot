package com.millennialmedia.intellibot.ide.structure;

import com.intellij.psi.PsiElement;
import com.millennialmedia.intellibot.RobotBundle;
import com.millennialmedia.intellibot.ide.icons.RobotIcons;
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
            return RobotIcons.HEADING;
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
            return RobotIcons.TEST_CASE;
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
            return RobotIcons.KEYWORD_DEFINITION;
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
            return RobotIcons.VARIABLE_DEFINITION;
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
