package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.DriverFactory;

public class Hooks {

    @Before
    public void setup() {
        DriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        if (scenario.isFailed()) {
            byte[] shot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(shot, "image/png", "failure");
        }
        DriverFactory.quitDriver();
    }

}
