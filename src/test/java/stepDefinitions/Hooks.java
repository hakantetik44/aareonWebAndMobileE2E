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
        this.platform = System.getProperty("platformName", ConfigReader.getProperty("platformName", "android")).toLowerCase();
        
        // Ajouter le nom de la plateforme au titre du scénario
        String platformName = platform.substring(0, 1).toUpperCase() + platform.substring(1);
        String originalName = scenario.getName();
        String newName = originalName + " - " + platformName;
        
        // Ajouter des informations sur la plateforme
        scenario.log("Plateforme de test: " + platform.toUpperCase());
        Allure.label("platform", platform);
        Allure.description("Plateforme de test: " + platform.toUpperCase() + "\n" + newName);
        
        System.out.println("\n=== Nouveau Scénario Commence: " + newName + " ===");
        System.out.println("Plateforme: " + platform);
        
        // Démarrer l'application pour ce scénario
        startApplication();
    }

    private void startApplication() {
        try {
            System.out.println("Démarrage de l'application pour le scénario - Plateforme: " + platform);
            
            WebDriver driver = Driver.getCurrentDriver();
            if (driver == null) {
                throw new RuntimeException("Impossible de démarrer le driver - Plateforme: " + platform);
            }
            
            scenario.log("Application démarrée avec succès: " + platform.toUpperCase());
            Allure.step("Application démarrée: " + platform.toUpperCase());
            
            System.out.println("Driver créé avec succès: " + platform);
            
            // Attendre que l'application soit prête
            Thread.sleep(2000);
            
        } catch (Exception e) {
            String errorMsg = String.format("Erreur lors du démarrage (%s): %s", platform, e.getMessage());
            System.err.println(errorMsg);
            e.printStackTrace();
            scenario.log(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Given("l'application Les Residences est ouverte")
    public void verifierApplicationOuverte() {
        WebDriver driver = Driver.getCurrentDriver();
        if (driver == null) {
            throw new RuntimeException("L'application n'est pas démarrée!");
        }
        System.out.println("L'application est ouverte et prête pour le test.");
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                try {
                    WebDriver driver = Driver.getCurrentDriver();
                    if (driver instanceof TakesScreenshot) {
                        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                        String screenshotName = String.format("capture-erreur-%s-%s", platform, scenario.getName());
                        scenario.attach(screenshot, "image/png", screenshotName);
                        Allure.addAttachment(screenshotName, "image/png", new String(screenshot));
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la capture d'écran: " + e.getMessage());
                }
            }
            
            String resultatTest = scenario.isFailed() ? "ÉCHEC" : "RÉUSSITE";
            System.out.println(String.format("\n=== Scénario Terminé: %s ===", scenario.getName()));
            System.out.println(String.format("Résultat: %s", resultatTest));
            
            scenario.log(String.format("Test terminé - Plateforme: %s, Résultat: %s", platform.toUpperCase(), resultatTest));
            Allure.step(String.format("Test terminé - Plateforme: %s, Résultat: %s", platform.toUpperCase(), resultatTest));
        } finally {
            // Forcer la fermeture de l'application
            System.out.println("Fermeture forcée de l'application...");
            try {
                WebDriver driver = Driver.getCurrentDriver();
                if (driver != null) {
                    driver.quit();
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture du driver: " + e.getMessage());
            }
            Driver.closeDriver();
            System.out.println("Application fermée avec succès.\n");
        }
    }
}