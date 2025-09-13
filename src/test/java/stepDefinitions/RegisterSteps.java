package stepDefinitions;

import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.RegisterPage;
import utils.ConfigReader;
import utils.DriverFactory;

public class RegisterSteps {
    WebDriver driver = DriverFactory.getDriver();
    RegisterPage registerPage = new RegisterPage(driver);

    @Given("I open the application")
    public void iOpenApplication() {
        driver.get(ConfigReader.getProperty("app.url"));
    }

    @When("I click on User Profile")
    public void clickUserProfile() {
        registerPage.clickUserProfile();
    }

    @When("I fill all mandatory fields from property file")
    public void fillMandatoryFieldsFromProperty() {
        String baseEmail = ConfigReader.getProperty("user.email");
        String uniqueEmail = makeUniqueEmail(baseEmail);

        registerPage.fillMandatoryFields(
                ConfigReader.getProperty("user.name"),
                uniqueEmail,
                ConfigReader.getProperty("user.password"),
                ConfigReader.getProperty("user.gender")
        );
    }


    @When("I click on register")
    public void clickRegister() {
        registerPage.clickRegisterLink();
    }

    @Then("I should see logout button in user profile")
    public void validateLogoutButton() {
        Assert.assertTrue(registerPage.isLogoutDisplayed());
    }


    private String makeUniqueEmail(String email) {
        if (email == null || !email.contains("@")) return System.currentTimeMillis()+"@example.com";
        String[] parts = email.split("@", 2);
        return parts[0] + "+" + System.currentTimeMillis() + "@" + parts[1];
    }
}
