package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class LoginPage extends BasePage {
    private final WebDriverWait wait;
    private final JavascriptExecutor js;
    private final Actions actions;

    private final By userIcon   = By.cssSelector("i.pe-7s-user-female, i[class*='pe-7s-user']");
    private final By dropdown   = By.cssSelector("div.account-dropdown");
    private final By loginLink  = By.cssSelector("div.account-dropdown a[href='/login']");


    private final By emailField    = By.id("email");
    private final By passwordField = By.id("password");
    private final By loginBtn      = By.xpath("//button[@type='submit' and normalize-space()='Login']");


    private final By accountHeaderMaybe = By.xpath(
            "//*[@id='root']//*[self::h1 or self::h2 or self::h3][contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'account')]"
    );

    public LoginPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
    }

    private void openAccountMenu() {
        WebElement icon = wait.until(ExpectedConditions.presenceOfElementLocated(userIcon));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(icon)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", icon);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdown));
    }

    public void goToLoginPageFromMenu() {
        openAccountMenu();
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
        link.click();
        wait.until(ExpectedConditions.urlContains("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
    }

    public void login(String email, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).clear();
        driver.findElement(emailField).sendKeys(email);
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginBtn).click();


        wait.until(ExpectedConditions.or(
                ExpectedConditions.invisibilityOfElementLocated(loginBtn),
                ExpectedConditions.not(ExpectedConditions.urlContains("/login"))
        ));
    }


    public boolean confirmLoggedInByMyAccountUrl() {
        String base = getBaseUrl();
        driver.navigate().to(base + "/my-account");


        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/my-account"),
                    ExpectedConditions.urlContains("/login")
            ));
        } catch (TimeoutException ignored) { }

        String url = driver.getCurrentUrl();
        if (url.contains("/my-account")) {

            try {
                wait.withTimeout(Duration.ofSeconds(3))
                        .until(ExpectedConditions.presenceOfElementLocated(accountHeaderMaybe));
            } catch (Exception ignored) { }
            return true;
        }
        return false;
    }

    private String getBaseUrl() {

        try {
            return utils.ConfigReader.getProperty("app.url").replaceAll("/+$", "");
        } catch (Throwable t) {

            String current = driver.getCurrentUrl();
            int i = current.indexOf("/", current.indexOf("//") + 2);
            return (i > 0 ? current.substring(0, i) : current).replaceAll("/+$", "");
        }
    }
}
