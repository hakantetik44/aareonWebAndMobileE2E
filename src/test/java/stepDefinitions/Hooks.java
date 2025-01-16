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
import utils.OS;

public class Hooks {
    private Scenario scenario;

    @Before
    public void setUp(Scenario scenario) {
        this.scenario = scenario;
        OS.OS = ConfigReader.getProperty("platformName", "Android");
    }

    @Given("l'application Les Residences est ouverte")
    public void verifierApplicationOuverte() {
        try {
            System.out.println("Démarrage de l'application...");
            if (OS.isAndroid()) {
                Driver.Android = Driver.getAndroidDriver();
                System.out.println("Driver Android créé avec succès");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du démarrage du driver: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            WebDriver driver = Driver.getCurrentDriver();
            if (driver != null && scenario.isFailed()) {
                if (driver instanceof TakesScreenshot) {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "capture-erreur");
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du teardown: " + e.getMessage());
        } finally {
            Driver.closeDriver();
        }
    }
}