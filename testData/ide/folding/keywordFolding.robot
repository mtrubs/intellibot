invalid
<fold text='*** Settings ***' expand='false'>*** Settings ***
Documentation     This is some demo text
Library           CalculatorLibrary</fold>

<fold text='*** Variables ***' expand='true'>*** Variables ***
${var1}  12345
${var2}  another variable</fold>

<fold text='*** Test Cases ***' expand='true'>*** Test Cases ***
<fold text='Addition' expand='true'>Addition
  [Tags]  Calculator
    Given calculator has been cleared
    When user types "1 + 1"
    And user pushes equals
    Then result is "2"</fold></fold>

#Subtraction
#  [Tags]  Calculator
#    TODO: implement me

<fold text='*** Keywords ***' expand='true'>*** Keywords ***
<fold text='Calculator has been cleared' expand='true'>Calculator has been cleared
    Push button    C</fold>

<fold text='User types "${expression}"' expand='true'>User types "${expression}"
    Push buttons    ${expression}</fold>

<fold text='User pushes equals' expand='true'>User pushes equals
    Push button    =</fold>

<fold text='Result is "${result}"' expand='true'>Result is "${result}"
    Result should be    ${result}</fold></fold>