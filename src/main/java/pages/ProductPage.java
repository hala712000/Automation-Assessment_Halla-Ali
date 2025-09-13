package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductPage extends BasePage {
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    public ProductPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        this.js = (JavascriptExecutor) driver;
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

    private String extractAmount(String s) {
        Matcher m = Pattern.compile("\\$?\\s*([0-9]+(?:\\.[0-9]{1,2})?)").matcher(s);
        return m.find() ? m.group(1) : null;
    }

    public void selectProduct(String productName) {

        By productLink = By.xpath(
                "//div[contains(@class,'product-content')]//h3/a[contains(normalize-space(.), '" + productName + "')]"
        );

        WebElement product = wait.until(ExpectedConditions.presenceOfElementLocated(productLink));


        try {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", product);
        } catch (Exception ignored) {}


        try {
            wait.until(ExpectedConditions.elementToBeClickable(product)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", product);
        }


        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'pro-details-cart')]//button")
        ));
    }




    public void openCart() {
        driver.navigate().to(baseUrl() + "/cart");
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".cart-main-area, .cart-area, .shopping-cart, [class*='cart']")
        ));
    }


    public void addToCart() {
        if (driver.getCurrentUrl().contains("/cart")) {
            driver.navigate().back();
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class,'pro-details-cart')]//button")
            ));
        }

        By addBtn = By.xpath(
                "//div[contains(@class,'pro-details-cart')]//button" +
                        "[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart')]"
        );

        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(addBtn));
        try { js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn); } catch (Exception ignored) {}
        try { wait.until(ExpectedConditions.elementToBeClickable(btn)).click(); }
        catch (Exception e) { js.executeScript("arguments[0].click();", btn); }


        try {
            wait.withTimeout(Duration.ofSeconds(3)).until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".Toastify__toast-body, .toast")),
                    ExpectedConditions.urlContains("/cart")
            ));
        } catch (Exception ignored) {
        } finally {
            wait.withTimeout(Duration.ofSeconds(12));
        }
    }


    public boolean isProductInCart(String name) {
        By nameInCart = By.xpath(
                "//*[contains(@class,'cart') or @id='cart' or self::body]" +
                        "//*[self::a or self::div or self::span][contains(normalize-space(.), '" + name + "')]"
        );
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(nameInCart));
            return true;
        } catch (TimeoutException te) {
            return false;
        }
    }

    public String getCartTotal() {
        By[] candidates = new By[] {
                By.cssSelector(".cart-total, .order-total, .total, #total"),
                By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total')][.//span or .//strong]")
        };
        for (By c : candidates) {
            try {
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(c));
                String amount = extractAmount(el.getText());
                if (amount != null) return amount;
            } catch (Exception ignored) {}
        }
        String amount = extractAmount(driver.getPageSource());
        return amount != null ? amount : "";
    }
}
