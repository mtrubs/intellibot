documentation
this is a sample file

*** Settings ***
# fun things going on here
Documentation  Test the account dashboard
...
...  and this goes to the next line
Documentation
  ...  all new line

Resource  kyle/web/db_advertiser_actions.robot
Resource  kyle/web/db_campaign_actions.robot
Resource  kyle/web/db_staff_actions.robot
Resource  kyle/web/ui_login_page.robot
Resource  kyle/web/ui_manage_accounts_page.robot
Resource  kyle_db_cleanup/kyle_cleanup.robot
Library  Selenium2Library  timeout=${ENV['selenium']['timeout']}  implicit_wait=${ENV['selenium']['implicit_wait']}
Library  db.orm.Orm
Library  OperatingSystem
Library  Collections

Force Tags  Kyle  Advertiser  Dashboard  Component  #Other Tag
Suite Teardown  This works

*** Variables ***

${Total_Requests}  97,000
${kw_timeout}  20 sec
${kw_retry_interval}  0.5 sec
@{some_letters}  A  B  C

*** Test Cases ***

Scenario: An admin can see the conversion trend
  [Tags]  Was Flickering
  [Setup]  Prepare advertiser "Robot_Company"
    Given Advertiser has performance data
    And I have an Account Manager  MANAGER

    When I login to Tapmatch as Staff  ${ACCOUNT_MANAGER.user.name}  ${ACCOUNT_MANAGER.user.password}
    Then The 7-day average should be  $0.09
  [Teardown]  Run Keywords  Close All Browsers  Clean Database

Scenario: This is also a keyword definition
  [Documentation]  adding another
  # just for fun
  ...  keyword will be classified correctly
    Given this sometimes works
    Then I will be happy  
    And I will be happy  12  123

*** Keywords ***

Clean Database
  [Documentation]  Cleans the database
  [Arguments]  ${defined}=${Total_Requests}
    ${var1} =  This works  1
    Clean  Kyle
    ${var2}  ${var3} =  This should work  2
    ${defined} Clean  Ike
#    Clean  Other Stuff
    Close All Browsers
    

This ${rate} works
    its a new ${rate} keyword
    run keyword if  ${a}=${b}  equal  not equal
  [Return]  ${Total_Requests}

I will be happy
    there is a smile on my face  ...