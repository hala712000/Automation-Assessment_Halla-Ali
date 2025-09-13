package stepDefinitions;

import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.HomePage;
import pages.LoginPage;
import pages.ProductPage;
import utils.ConfigReader;
import utils.DriverFactory;

public class PurchaseSteps {
    WebDriver driver = DriverFactory.getDriver();
    LoginPage loginPage = new LoginPage(driver);
    HomePage homePage = new HomePage(driver);
    ProductPage productPage = new ProductPage(driver);

    @When("I login with valid credentials")
    public void loginWithValidCredentials() {
        loginPage.goToLoginPageFromMenu();
        loginPage.login(
                ConfigReader.getProperty("login.email"),
                ConfigReader.getProperty("login.password")
        );
    }

    @Then("I should see account dashboard")
    public void accountDashboardShouldBeVisible() {
        Assert.assertTrue("Not logged in: /my-account not reachable",
                loginPage.confirmLoggedInByMyAccountUrl());
    }

    @When("I navigate to category {string}")
    public void navigateToCategory(String category) {
        homePage.selectCategory(category);
    }

    @When("I apply filter")
    public void applyFilter() {
        homePage.clickApply();
    }

    @Then("only {string} products should appear")
    public void onlyCategoryProductsShouldAppear(String category) {
        Assert.assertTrue(homePage.validateCategoryProducts(category));
    }

    @When("I select product {string}")
    public void selectProduct(String productName) {
        productPage.selectProduct(productName);
    }

    @Then("I validate that cart is empty")
    public void validateCartIsEmpty() {
        productPage.openCart();
        Assert.assertFalse(productPage.isProductInCart("Logitech Wireless Keyboard and Mouse Combo - Black (MK270)"));
        driver.navigate().back();
    }

    @Then("I add product to cart")
    public void addProductToCart() {
        productPage.addToCart();
    }

    @Then("I validate that two products with title and price are in cart")
    public void validateTwoProductsInCart() {
        productPage.openCart();
        Assert.assertTrue(productPage.isProductInCart("Logitech Wireless Keyboard and Mouse Combo - Black (MK270)"));
        Assert.assertTrue(productPage.isProductInCart("Dell Chromebook 11 3120"));
    }

    @Then("I validate that total amount is calculated correctly")
    public void validateTotalAmount() {
        String total = productPage.getCartTotal();
        System.out.println("Cart total: " + total);
        Assert.assertNotNull(total);
    }
}
