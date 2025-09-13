package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;



public class RegisterPage extends BasePage {

    private final By userProfileIcon = By.cssSelector("i.pe-7s-user-female, i[class*='pe-7s-user']");
    private final By dropdownList    = By.cssSelector("div.account-dropdown");
    private final By registerLink    = By.cssSelector("div.account-dropdown a[href='/register']");

    private final By nameField       = By.id("username");
    private final By emailField      = By.id("email");
    private final By passwordField   = By.id("password");
    private final By genderDropdown = By.id("gender");
    private final By registerBtn     = By.cssSelector("button.w-100.submit-btn.my-3");


    private final By logoutBtn = By.xpath("//li[normalize-space()='Logout']");

    private final JavascriptExecutor js;

    public RegisterPage(WebDriver driver) {
        super(driver);
        this.js = (JavascriptExecutor) driver;
    }



    private boolean onRegisterPage() {
        try { return driver.getCurrentUrl().contains("/register"); }
        catch (Exception e) { return false; }
    }

    private void openAccountMenuStable() {

        WebElement icon = wait.until(ExpectedConditions.presenceOfElementLocated(userProfileIcon));


        try {
            wait.until(ExpectedConditions.elementToBeClickable(icon)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", icon);
        }


        try {
            new org.openqa.selenium.interactions.Actions(driver)
                    .moveToElement(icon)
                    .pause(java.time.Duration.ofMillis(200))
                    .perform();
        } catch (Exception ignored) {}


        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownList));
    }


    public void clickUserProfile() {
        openAccountMenuStable();
    }

    public void ensureOnRegisterPage() {
        if (driver.getCurrentUrl().contains("/register")) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
            return;
        }

        final int MAX_TRIES = 3;
        for (int attempt = 1; attempt <= MAX_TRIES; attempt++) {
            try {
                openAccountMenuStable();

                WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(registerLink));


                try {
                    new org.openqa.selenium.interactions.Actions(driver)
                            .moveToElement(link)
                            .pause(java.time.Duration.ofMillis(120))
                            .perform();
                } catch (Exception ignored) {}


                try { js.executeScript("arguments[0].scrollIntoView({block:'center'});", link); } catch (Exception ignored) {}


                try {
                    new org.openqa.selenium.interactions.Actions(driver)
                            .moveToElement(link)
                            .pause(java.time.Duration.ofMillis(80))
                            .click(link)
                            .perform();
                } catch (org.openqa.selenium.ElementClickInterceptedException e) {

                    try {
                        new org.openqa.selenium.interactions.Actions(driver)
                                .moveToElement(link)
                                .pause(java.time.Duration.ofMillis(60))
                                .sendKeys(org.openqa.selenium.Keys.ENTER)
                                .perform();
                    } catch (Exception ignored2) {}


                    try { js.executeScript("arguments[0].click();", link); } catch (Exception ignored3) {}
                }


                wait.until(ExpectedConditions.urlContains("/register"));
                wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
                return; // success

            } catch (Exception retry) {

                try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            }
        }


        throw new RuntimeException("Failed to open /register from account dropdown after " + MAX_TRIES + " attempts.");
    }


    public void fillMandatoryFields(String name, String email, String password, String gender) {
        ensureOnRegisterPage();

        WebElement nameEl = wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
        nameEl.clear();
        nameEl.sendKeys(name);

        WebElement emailEl = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        emailEl.clear();
        emailEl.sendKeys(email);

        WebElement passEl = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        passEl.clear();
        passEl.sendKeys(password);


        WebElement genderEl = wait.until(ExpectedConditions.visibilityOfElementLocated(genderDropdown));
        Select select = new Select(genderEl);
        try {
            select.selectByValue(gender.toLowerCase()); // male/female/other
        } catch (Exception e) {

            select.selectByIndex(1);
        }
    }


    public void clickRegisterLink() {
        if (!onRegisterPage()) {
            ensureOnRegisterPage();
            return;
        }

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(registerBtn));
        try { btn.click(); } catch (Exception e) { js.executeScript("arguments[0].click();", btn); }


        clickUserProfile();
        wait.until(ExpectedConditions.visibilityOfElementLocated(logoutBtn));
    }

    public boolean isLogoutDisplayed() {
        clickUserProfile();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(logoutBtn)).isDisplayed();
    }

}
