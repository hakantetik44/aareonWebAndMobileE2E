package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.qameta.allure.Allure;
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
        
        // Add platform information to Cucumber report
        scenario.log("Test Platform: " + platform.toUpperCase());
        
        // Add platform information to Allure report
        Allure.label("platform", platform);
        Allure.description("Test Platform: " + platform.toUpperCase() + "\n" + scenario.getName());
        
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
            
            // Add platform information to test step
            scenario.log("Application started successfully on " + platform.toUpperCase());
            Allure.step("Application started on " + platform.toUpperCase());
            
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
                    String screenshotName = String.format("error-screenshot-%s-%s", platform, scenario.getName());
                    scenario.attach(screenshot, "image/png", screenshotName);
                    
                    // Add screenshot to Allure report
                    Allure.addAttachment(screenshotName, "image/png", new String(screenshot));
                }
            } catch (Exception e) {
                System.err.println("Error during screenshot capture: " + e.getMessage());
            }
        }
        
        // Add final status to reports
        String testResult = scenario.isFailed() ? "FAILED" : "PASSED";
        scenario.log(String.format("Test completed on %s - Status: %s", platform.toUpperCase(), testResult));
        Allure.step(String.format("Test completed on %s with status: %s", platform.toUpperCase(), testResult));
        
        Driver.closeDriver();
    }
}