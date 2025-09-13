package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class HomePage extends BasePage {
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    public HomePage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        this.js = (JavascriptExecutor) driver;
    }


    private static final By PRODUCT_NAME = By.cssSelector(".product-content h3 a");
    private static final By APPLY_BUTTON = By.xpath("//button[@id='apply' or contains(normalize-space(.),'Apply')]");
    private static final By PRODUCT_GRID_ANY = By.cssSelector(".product-content h3 a, [data-testid='product-card']");
    private static final By FILTER_PANEL_HINT = By.xpath(
            "//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'filter') or contains(@class,'filter')]"
    );

    private By categoryButton(String category) {
        String lower = category.toLowerCase();
        return By.xpath(
                "//*[self::button or self::label or self::a or self::div]" +
                        "[contains(translate(normalize-space(string(.)),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + lower + "')]"
        );
    }


    private String baseUrl() {
        try {
            return utils.ConfigReader.getProperty("app.url").replaceAll("/+$", "");
        } catch (Throwable t) {
            String current = driver.getCurrentUrl();
            int i = current.indexOf("/", current.indexOf("//") + 2);
            return (i > 0 ? current.substring(0, i) : current).replaceAll("/+$", "");
        }
    }


    public void ensureOnShopPage() {
        String mustBe = baseUrl() + "/shop-grid-standard";
        if (!driver.getCurrentUrl().startsWith(mustBe)) {
            driver.navigate().to(mustBe);
        }
        new WebDriverWait(driver, Duration.ofSeconds(12)).until(
                ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(PRODUCT_GRID_ANY),
                        ExpectedConditions.presenceOfElementLocated(FILTER_PANEL_HINT)
                )
        );
    }


    public void selectCategory(String category) {
        ensureOnShopPage();

        By btnLocator = categoryButton(category);
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnLocator));

        try { js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn); } catch (Exception ignored) {}

        try { btn.click(); }
        catch (ElementNotInteractableException e) { js.executeScript("arguments[0].click();", btn); }
        catch (Exception e) { js.executeScript("arguments[0].click();", btn); }


        try { wait.withTimeout(Duration.ofSeconds(2)).until(ExpectedConditions.presenceOfElementLocated(PRODUCT_GRID_ANY)); }
        catch (Exception ignored) { }
        finally { wait.withTimeout(Duration.ofSeconds(12)); }
    }

    public void clickApply() {
        ensureOnShopPage();
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(APPLY_BUTTON));
        try { js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn); } catch (Exception ignored) {}
        try { btn.click(); } catch (Exception e) { js.executeScript("arguments[0].click();", btn); }
    }

    public boolean validateCategoryProducts(String category) {
        String lower = category.toLowerCase();


        try {
            By activeFilter = By.xpath(
                    "//*[self::button or self::a or self::label or self::div]" +
                            "[contains(translate(normalize-space(string(.)),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + lower + "')]" +
                            "[contains(@class,'active') or contains(@class,'selected') or contains(@class,'is-checked') or @aria-pressed='true' or @aria-selected='true']"
            );
            if (!driver.findElements(activeFilter).isEmpty()) {
                return true; //
            }
        } catch (Exception ignored) {}


        List<WebElement> items = driver.findElements(By.cssSelector(".product-content h3 a"));
        if (items.isEmpty()) return false;

        for (WebElement p : items) {
            if (p.getText().toLowerCase().contains(lower)) {
                return true;
            }
        }


        System.out.println("❌ لم يتم تفعيل الفلتر '" + category + "' ولم يظهر أي منتج يطابقه.");
        return false;
    }

}
