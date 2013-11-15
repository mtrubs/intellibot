*** Settings ***
# fun things going on here
Documentation  Test the account dashboard

Resource  kyle/web/db_advertiser_actions.txt
Resource  kyle/web/db_campaign_actions.txt
Resource  kyle/web/db_staff_actions.txt
Resource  kyle/web/ui_login_page.txt
Resource  kyle/web/ui_manage_accounts_page.txt
Resource  kyle_db_cleanup/kyle_cleanup.txt
Library  Selenium2Library  timeout=${ENV['selenium']['timeout']}  implicit_wait=${ENV['selenium']['implicit_wait']}
Library  db.orm.Orm
Library  OperatingSystem
Library  Collections

Force Tags  Kyle  Advertiser  Dashboard  Component

*** Test Cases ***

Scenario: Test that the numeric columns are sorted largest to smallest by default
  [Tags]  Sort  Sanity
    GIVEN I login to tapmatch as admin
    :FOR  ${index}  IN RANGE  11  25
    \    WHEN Click Element  xpath=//*[@id="users"]/thead/tr/th[${index}]/a
    \    ${classes}  Get Element Attribute  xpath=//*[@id="users"]/thead/tr/th[${index}]@class
    \    THEN Should Contain  ${classes}  order2
  [Teardown]  Run Keywords  Close All Browsers  Clean Database

Scenario: An admin can see the conversion trend
  [Tags]  Was Flickering
  [Setup]  Prepare advertiser "Robot_Company"
    GIVEN Advertiser has performance data
    AND I have an Account Manager  MANAGER
    WHEN I login to Tapmatch as Staff  ${ACCOUNT_MANAGER.user.name}  ${ACCOUNT_MANAGER.user.password}
    AND Staff user goes to All Accounts view
    AND I view the recent advertiser performance trend
    THEN The 7-day average should be  $0.09
  [Teardown]  Run Keywords  Close All Browsers  Clean Database

Scenario: The manage accounts page is properly filters by "Accounts w/Performance Data"
    GIVEN Prepare advertiser "comp_w_perf_data"
    AND Advertiser has performance data
    AND Prepare advertiser "comp_w_oas_camp" with OAS campaign
    AND Prepare advertiser "comp_w_old_perf"
    AND Advertiser has performance data  20  27
    AND I have an Account Manager  MANAGER
    WHEN I login to Tapmatch as Staff  ${ACCOUNT_MANAGER.user.name}  ${ACCOUNT_MANAGER.user.password}  false
    AND Staff user goes to All Accounts view  false
    AND The daterangepicker is set to  Last 7 days
    THEN The view is filtered with performance data
    AND The advertiser "comp_w_perf_data" is displayed
    AND The advertiser "comp_w_oas_camp" is displayed
    AND The advertiser "comp_w_old_perf" is not displayed
    WHEN Toggle View With Performance Data filter
    THEN The advertiser "comp_w_perf_data" is displayed
    AND The advertiser "comp_w_oas_camp" is displayed
    AND The advertiser "comp_w_old_perf" is displayed
  [Teardown]  Run Keywords  Close All Browsers  Clean Database