Feature: New User SignUp

  Scenario Outline: New User with single exact matching address and general shift time
    
    Given: Employee is enrolling for the first time 
    When: He picks his pick up address as "regency cosmos, baner, pune" 
    And: Enters 
    |Name    |Abhishek|
    |Mobile  |9632511423|
    |Email   |abhishekkumar.singh|
    |Password|password|
    Then: He should see the dashboard with entire month in time as "9:30 am" and out time as "6:30 pm"

