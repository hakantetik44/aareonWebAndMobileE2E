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
        System.out.println("Test başlıyor - Platform: " + OS.OS);
    }

    @Given("l'application Les Residences est ouverte")
    public void verifierApplicationOuverte() {
        try {
            System.out.println("Démarrage de l'application sur " + OS.OS);
            switch (OS.OS.toLowerCase()) {
                case "android":
                    Driver.Android = Driver.getAndroidDriver();
                    break;
                case "ios":
                    // iOS driver initialization will be implemented
                    break;
                case "web":
                    Driver.Web = Driver.getWebDriver();
                    break;
                default:
                    throw new RuntimeException("Platform non supportée: " + OS.OS);
            }
            System.out.println("Driver créé avec succès pour " + OS.OS);
        } catch (Exception e) {
            String errorMsg = String.format("Erreur lors du démarrage sur %s: %s", OS.OS, e.getMessage());
            System.err.println(errorMsg);
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
                    scenario.attach(screenshot, "image/png", "capture-erreur-" + OS.OS);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la capture d'écran: " + e.getMessage());
            }
        }
        Driver.closeDriver();
    }
}