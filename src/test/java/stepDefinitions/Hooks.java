package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;
import utils.Driver;

public class Hooks {
    private Scenario scenario;
    private String platform;

    @Before
    public void setUp(Scenario scenario) {
        this.scenario = scenario;
        // Get platform from system property or config
        this.platform = System.getProperty("platformName", ConfigReader.getProperty("platformName", "android")).toLowerCase();
        System.out.println("Test starting - Platform: " + platform);
    }

    @Given("l'application Les Residences est ouverte")
    public void verifierApplicationOuverte() {
        try {
            System.out.println("Starting application on " + platform);
            
            // Get the appropriate driver based on platform
            WebDriver driver = Driver.getCurrentDriver();
            if (driver == null) {
                throw new RuntimeException("Driver could not be initialized for platform: " + platform);
            }
            System.out.println("Driver successfully created for " + platform);
            
        } catch (Exception e) {
            String errorMsg = String.format("Error during startup on %s: %s", platform, e.getMessage());
            System.err.println(errorMsg);
            e.printStackTrace();
            scenario.log(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            try {
                WebDriver driver = Driver.getCurrentDriver();
                if (driver instanceof TakesScreenshot) {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "error-screenshot-" + platform);
                }
            } catch (Exception e) {
                System.err.println("Error during screenshot capture: " + e.getMessage());
            }
        }
        Driver.closeDriver();
    }
}