*** Settings ***

Library  Selenium2Library  ${variable}

*** Variables ***

${variable}  1234

*** Test Cases ***
Do It Up
    User types "water"
    User then types falls
    "are" is then entered by user
    pretty is entered by user

*** Keywords ***

User types "${text}"
    Enter  ${text}

User then types ${text}
    Enter  ${text}

${text} is entered by user
    Enter  ${text}

"${text}" is then entered by user
    Enter  ${text}

User types "${a}" and "${b}"
    ${a} is entered by user
    ${b} is entered by user

Enter
  [Arguments]  ${text}
    ${results}  log  ${text}
  [Return]  ${results}