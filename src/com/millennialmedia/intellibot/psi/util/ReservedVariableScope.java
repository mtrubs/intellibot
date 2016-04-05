package com.millennialmedia.intellibot.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.millennialmedia.intellibot.psi.element.*;
import com.millennialmedia.intellibot.psi.ref.PythonResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 * @since 2016-03-29
 */
public enum ReservedVariableScope {

    Global {
        @Override
        public boolean isInScope(@NotNull PsiElement position) {
            // everywhere
            return true;
        }
    },
    TestCase {
        @Override
        public boolean isInScope(@NotNull PsiElement position) {
            // only in test cases
            return isArgument(position) && isInTestCase(position) || TestTeardown.isInScope(position);
        }
    },
    KeywordTeardown {
        @Override
        public boolean isInScope(@NotNull PsiElement position) {
            // only in teardown for keywords
            return isArgument(position) && isInKeywordTeardown(position);
        }
    },
    TestTeardown {
        @Override
        public boolean isInScope(@NotNull PsiElement position) {
            // only in teardown for test cases
            return isArgument(position) && isInTestTeardown(position);
        }
    },
    SuiteTeardown {
        @Override
        public boolean isInScope(@NotNull PsiElement position) {
            // only in teardown for suites
            return isArgument(position) && isInSuiteTeardown(position);
        }
    };

    /**
     * Determines if the given element is a part of an Argument.
     *
     * @param position the element in question.
     * @return true if this or one of its parents is an Argument.
     */
    private static boolean isArgument(@NotNull PsiElement position) {
        // either this is an Argument or it has a parent that is
        return position instanceof Argument ||
                PsiTreeUtil.getParentOfType(position, Argument.class) != null;
    }

    /**
     * Determines if the given element is a part of the Settings header.
     *
     * @param position the element in question.
     * @return true if this or one of its parents is the Settings header.
     */
    private static boolean isInSettings(@NotNull PsiElement position) {
        KeywordStatement keyword = getKeyword(position);
        if (keyword == null) {
            return false;
        }
        // and that keyword is in the test cases heading
        Heading heading = PsiTreeUtil.getParentOfType(keyword, Heading.class);
        return heading != null && heading.isSettings();
    }

    /**
     * Determines if the given element is a part of the Test Cases header.
     *
     * @param position the element in question.
     * @return true if this or one of its parents is the Test Cases header.
     */
    private static boolean isInTestCase(@NotNull PsiElement position) {
        KeywordStatement keyword = getKeyword(position);
        if (keyword == null) {
            return false;
        }
        // and that keyword is in the test cases heading
        Heading heading = PsiTreeUtil.getParentOfType(keyword, Heading.class);
        return heading != null && heading.containsTestCases();
    }

    /**
     * Determines if the given element is a part of the Keywords header.
     *
     * @param position the element in question.
     * @return true if this or one of its parents is the Keywords header.
     */
    private static boolean isInKeywords(@NotNull PsiElement position) {
        KeywordStatement keyword = getKeyword(position);
        if (keyword == null) {
            return false;
        }
        // and that keyword is in the test cases heading
        Heading heading = PsiTreeUtil.getParentOfType(keyword, Heading.class);
        return heading != null && heading.containsKeywordDefinitions();
    }

    /**
     * Determines if the given element is a part of the suite teardown.
     *
     * @param position the element in question.
     * @return true if this is part of the suite teardown.
     */
    private static boolean isInSuiteTeardown(@NotNull PsiElement position) {
        if (isInSettings(position)) {
            // check that we are next to a suite teardown setting
            KeywordStatement keyword = getKeyword(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof Setting) {
                return ((Setting) sibling).isSuiteTeardown();
            }
        }
        return false;
    }

    /**
     * Determines if the given element is a part of the test teardown.
     *
     * @param position the element in question.
     * @return true if this is part of the test teardown.
     */
    private static boolean isInTestTeardown(@NotNull PsiElement position) {
        // this can either be in the settings of the file or the bracket settings of the definition
        if (isInSettings(position)) {
            // check that we are next to a test teardown setting
            KeywordStatement keyword = getKeyword(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof Setting) {
                return ((Setting) sibling).isTestTeardown();
            }
        } else if (isInTestCase(position)) {
            // check that we are next to a teardown bracket setting
            KeywordStatement keyword = getKeyword(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof BracketSetting) {
                return ((BracketSetting) sibling).isTeardown();
            }
        }
        return false;
    }

    /**
     * Gets the previous statement of the given element ignoring whitespace.
     *
     * @param position the element in question.
     * @return the previous statement (sibling) of the element or null if there not one.
     */
    @Nullable
    private static PsiElement getPreviousStatement(@NotNull PsiElement position) {
        PsiElement sibling = position.getPrevSibling();
        if (sibling instanceof PsiWhiteSpace) {
            sibling = sibling.getPrevSibling();
        }
        return sibling;
    }

    /**
     * Determines if the given element is a part of the keyword teardown.
     *
     * @param position the element in question.
     * @return true if this is part of the keyword teardown.
     */
    private static boolean isInKeywordTeardown(@NotNull PsiElement position) {
        if (isInKeywords(position)) {
            // check that we are next to a teardown bracket setting
            KeywordStatement keyword = getKeyword(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof BracketSetting) {
                return ((BracketSetting) sibling).isTeardown();
            }
        }
        return false;
    }

    /**
     * Gets the keyword statement element associated with this element.
     *
     * @param position the element in question.
     * @return either this element or one of its parents that is a keyword statement; else null.
     */
    @Nullable
    private static KeywordStatement getKeyword(@NotNull PsiElement position) {
        // either we are a keyword or we have a parent that is
        return position instanceof KeywordStatement ?
                (KeywordStatement) position :
                PsiTreeUtil.getParentOfType(position, KeywordStatement.class);
    }

    @Nullable
    public PsiElement getVariable(@NotNull Project project) {
        return PythonResolver.findVariable("GLOBAL_VARIABLES", project);
    }

    public abstract boolean isInScope(@NotNull PsiElement position);
}
