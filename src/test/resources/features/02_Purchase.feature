Feature: Product Purchase

  Scenario: Verify that two products are purchased successfully
    Given I open the application
    When I login with valid credentials
    Then I should see account dashboard
    When I navigate to category "Keyboard"
    And I apply filter
    Then only "Keyboard" products should appear
    When I select product "Logitech Wireless Keyboard and Mouse Combo - Black (MK270)"
    Then I validate that cart is empty
    And I add product to cart
    When I navigate to category "Laptop"
    And I apply filter
    And I select product "Dell Chromebook 11 3120 (11.6\", Intel Celeron N2840, 4GB RAM, 16GB SSD, Latest Chromebook OS) - Refurbished"
    And I add product to cart
    Then I validate that two products with title and price are in cart
    And I validate that total amount is calculated correctly
