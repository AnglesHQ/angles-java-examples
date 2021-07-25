Feature: Angles GitLab Page

  Scenario: Navigate to gitlab page and do a visual comparison
    Given user navigates to github page by opening Chrome
    When and the user waits for the github link to appear
    Then the visual mismatch for view angles_github_page should not exceed 0.1 percent

