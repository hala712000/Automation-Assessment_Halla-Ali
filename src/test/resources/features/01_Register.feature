Feature: User Registration

  Scenario: Verify that user can sign up successfully
    Given I open the application
    When I click on User Profile
    And I fill all mandatory fields from property file
    And I click on register
    Then I should see logout button in user profile
