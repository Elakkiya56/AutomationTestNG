Feature: Test Login
  verify login

  Scenario: login with valid credentials
    Given launch the URL
    When login with valid credentials
    And click on login btn
    Then validate the login
