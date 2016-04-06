*** Settings ***

Library  Selenium2Library  ${variable}  ${${variable}.length}

*** Variables ***

${variable}  1234

*** Test Cases ***
Do It Up
    User types "water"
    User then types falls
    "are" is then entered by user
    pretty is entered by user

Do It Up Again
    User types "${variable}"
    User then types ${variable}
    "${variable}" is then entered by user
    ${variable} is entered by user
    User types "${variable}" and "${variable}"

*** Keywords ***

${a} is entered by user
    Enter  ${a}

User types "${b}" and "${c}"
    ${b} is entered by user
    ${c} is entered by user

"${d}" is then entered by user
    Enter  ${d}

User then types ${e}
    Enter  ${e}
    log  ${${e}[0]}

User types "${f}"
    Enter  ${f}
  [Return]  ${${f}}

Enter
  [Arguments]  ${g}
    ${h}=  log  ${g}
    ${${h}}  ${${g}}=  log  stuff
  [Return]  ${h}

Leave
  [Arguments]  ${i}=1
  ...          ${j}=2
    Log  ${i}  ${j}