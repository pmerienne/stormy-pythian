Feature: Root path feature
  As a user of stormy-pythian
  I want to see the welcome page at /
  So that I know that stormy-pythian is up and running

  Scenario: Load stormy-pyhtian at base path
    When I visit the root page
    Then I should see "Stormy Pythian"