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

    @Given("Les Residences uygulamasi acik durumda")
    public void lesResidencesUygulamasiAcikDurumda() {
        try {
            System.out.println("Uygulama başlatılıyor...");
            if (OS.isAndroid()) {
                Driver.Android = Driver.getAndroidDriver();
                System.out.println("Android driver başarıyla oluşturuldu");
            }
        } catch (Exception e) {
            System.out.println("Driver başlatma hatası: " + e.getMessage());
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
                    scenario.attach(screenshot, "image/png", "hata-ekran-goruntusu");
                }
            }
        } catch (Exception e) {
            System.out.println("Teardown hatası: " + e.getMessage());
        } finally {
            Driver.closeDriver();
        }
    }
} 