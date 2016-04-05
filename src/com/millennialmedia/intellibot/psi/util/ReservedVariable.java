package com.millennialmedia.intellibot.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author mrubino
 * @since 2016-03-29
 */
public enum ReservedVariable {

    // Scope Everywhere
    CUR_DIR("${CUR_DIR}", ReservedVariableScope.Global), // An absolute path to the directory where the test data file is located. This variable is case-sensitive.
    TEMP_DIR("${TEMP_DIR}", ReservedVariableScope.Global), // An absolute path to the system temporary directory. In UNIX-like systems this is typically /tmp, and in Windows c:\Documents and Settings\<user>\Local Settings\Temp.
    EXEC_DIR("${EXEC_DIR}", ReservedVariableScope.Global), // An absolute path to the directory where test execution was started from.
    PATH_SEPARATOR("${/}", ReservedVariableScope.Global), // The system directory path separator. / in UNIX-like systems and \ in Windows.
    PATH_ELEMENT("${:}", ReservedVariableScope.Global), // The system path element separator. : in UNIX-like systems and ; in Windows.
    NEW_LINE("${\\n}", ReservedVariableScope.Global), // The system line separator. \n in UNIX-like systems and \r\n in Windows.
    SPACE("${SPACE}", ReservedVariableScope.Global), // String with space: " "
    SUPER_SPACE("${SPACE_*_2}", ReservedVariableScope.Global), // String with two spaces: "  "
    EMPTY("${EMPTY}", ReservedVariableScope.Global), // Empty String
    EMPTY_LIST("@{EMPTY}", ReservedVariableScope.Global), // Empty list
    EMPTY_DICTIONARY("&{EMPTY}", ReservedVariableScope.Global), // Empty dictionary
    TRUE("${True}", ReservedVariableScope.Global), // Boolean true
    FALSE("${False}", ReservedVariableScope.Global), // Boolean false
    NONE("${None}", ReservedVariableScope.Global), // Python None
    NULL("${null}", ReservedVariableScope.Global), // Java null
    SUITE_NAME("${SUITE_NAME}", ReservedVariableScope.Global), // The full name of the current test suite.
    SUITE_SOURCE("${SUITE_SOURCE}", ReservedVariableScope.Global), // An absolute path to the suite file or directory.
    SUITE_DOCUMENTATION("${SUITE_DOCUMENTATION}", ReservedVariableScope.Global), // The documentation of the current test suite. Can be set dynamically using using Set Suite Documentation keyword.
    SUITE_METADATA("&{SUITE_METADATA}", ReservedVariableScope.Global), // The free metadata of the current test suite. Can be set using Set Suite Metadata keyword.
    OUTPUT_DIR("${OUTPUT_DIR}", ReservedVariableScope.Global), // An absolute path to the output directory.
    OUTPUT_FILE("${OUTPUT_FILE}", ReservedVariableScope.Global), // An absolute path to the output file.
    REPORT_FILE("${REPORT_FILE}", ReservedVariableScope.Global), // An absolute path to the report file or string NONE when no report is created.
    LOG_FILE("${LOG_FILE}", ReservedVariableScope.Global), // An absolute path to the log file or string NONE when no log file is created.
    LOG_LEVEL("${LOG_LEVEL}", ReservedVariableScope.Global), // Current log level.
    DEBUG_FILE("${DEBUG_FILE}", ReservedVariableScope.Global), // An absolute path to the debug file or string NONE when no debug file is created.
    // Scope: Test Case
    PREV_TEST_NAME("${PREV_TEST_NAME}", ReservedVariableScope.TestCase), // The name of the previous test case, or an empty string if no tests have been executed yet.
    PREV_TEST_STATUS("${PREV_TEST_STATUS}", ReservedVariableScope.TestCase), // The status of the previous test case: either PASS, FAIL, or an empty string when no tests have been executed.
    PREV_TEST_MESSAGE("${PREV_TEST_MESSAGE}", ReservedVariableScope.TestCase), // The possible error message of the previous test case.
    TEST_NAME("${TEST_NAME}", ReservedVariableScope.TestCase), // The name of the current test case.
    TEST_TAGS("@{TEST_TAGS}", ReservedVariableScope.TestCase), // Contains the tags of the current test case in alphabetical order. Can be modified dynamically using Set Tags and Remove Tags keywords.
    TEST_DOCUMENTATION("${TEST_DOCUMENTATION}", ReservedVariableScope.TestCase), // The documentation of the current test case. Can be set dynamically using using Set Test Documentation keyword.
    // Scope Test Teardown
    TEST_STATUS("${TEST_STATUS}", ReservedVariableScope.TestTeardown), // The status of the current test case, either PASS or FAIL.
    TEST_MESSAGE("${TEST_MESSAGE}", ReservedVariableScope.TestTeardown), // The message of the current test case.
    // Scope: Keyword Teardown
    KEYWORD_STATUS("${KEYWORD_STATUS}", ReservedVariableScope.KeywordTeardown), // The status of the current keyword, either PASS or FAIL.
    KEYWORD_MESSAGE("${KEYWORD_MESSAGE}", ReservedVariableScope.KeywordTeardown), // The possible error message of the current keyword.
    // Scope: Suite Teardown
    SUITE_STATUS("${SUITE_STATUS}", ReservedVariableScope.SuiteTeardown), // The status of the current test suite, either PASS or FAIL.
    SUITE_MESSAGE("${SUITE_MESSAGE}", ReservedVariableScope.SuiteTeardown), // The full message of the current test suite, including statistics.
    ;

    private static final String SCALAR = "${%s}";
    private final String variable;
    private final ReservedVariableScope scope;

    ReservedVariable(@NotNull String variable, @NotNull ReservedVariableScope scope) {
        this.variable = variable;
        this.scope = scope;
    }

    @NotNull
    public static String wrapToScalar(@NotNull String text) {
        return String.format(SCALAR, text);
    }

    @NotNull
    public String getVariable() {
        return this.variable;
    }

    @NotNull
    public ReservedVariableScope getScope() {
        return this.scope;
    }

    @Nullable
    public PsiElement getVariable(@NotNull Project project) {
        return this.scope.getVariable(project);
    }
}
