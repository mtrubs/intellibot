invalid
*** Settings ***
Documentation     This is some demo text
Library           CalculatorLibrary

*** Variables ***
${var1}  12345
${<caret>var2}  another variable

*** Test Cases ***
Addition
  [Tags]  Calculator
    Given calculator has been cleared
    When user types "1 + 1"
    And user pushes equals
    Then result is "2"

Subtraction
  [Tags]  Calculator
    Given calculator has been cleared
    When user types "2 - 1"
    And user pushes equals
    Then result is "1"

*** Keywords ***
Calculator has been cleared
    Push button    C
    Log  ${var2}

User types "${expression}"
    Push buttons    ${expression}

User pushes equals
    Push button    =

Result is "${result}"
    Result should be    ${result}