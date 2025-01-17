package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.WebDriver;
import java.net.URL;
import java.time.Duration;

public class Driver {
    private Driver() {
    }

    public static AppiumDriver Android;
    public static AppiumDriver iOS;

    public static AppiumDriver getAndroidDriver() {
        if (Android == null) {
            try {
                String appiumUrl = System.getProperty("appium.server.url", "http://localhost:4723");
                System.out.println("Démarrage du driver Android... Appium URL: " + appiumUrl);
                
                UiAutomator2Options options = new UiAutomator2Options();
                options.setPlatformName("Android");
                options.setAutomationName("UiAutomator2");
                options.setDeviceName("emulator-5554");
                options.setAppPackage("fr.aareon.lesresidences.bis");
                options.setAppActivity("fr.aareon.lesresidences.bis.MainActivity");
                options.setNoReset(false);  
                options.setAutoGrantPermissions(true);
                options.setNewCommandTimeout(Duration.ofSeconds(60));
                options.setAdbExecTimeout(Duration.ofSeconds(60));

                System.out.println("Connexion à Appium avec les options: " + options);
                Android = new AppiumDriver(new URL(appiumUrl), options);
                Android.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                System.out.println("Driver Android créé avec succès!");

            } catch (Exception e) {
                System.err.println("Erreur lors de la création du driver Android: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Impossible de créer le driver Android", e);
            }
        }
        return Android;
    }

    public static AppiumDriver getIOSDriver() {
        if (iOS == null) {
            try {
                XCUITestOptions options = new XCUITestOptions()
                        .setPlatformName("iOS")
                        .setAutomationName("XCUITest")
                        .setPlatformVersion("16.0")
                        .setDeviceName("iPhone 14")
                        .setBundleId("fr.aareon.lesresidences.bis")
                        .setNoReset(true)
                        .setAutoAcceptAlerts(true);

                iOS = new AppiumDriver(new URL("http://127.0.0.1:4723"), options);
                iOS.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                System.out.println("iOS driver créé avec succès!");

            } catch (Exception e) {
                System.err.println("Erreur lors de la création du driver iOS: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Impossible de créer le driver iOS", e);
            }
        }
        return iOS;
    }

    public static WebDriver getCurrentDriver() {
        String platform = System.getProperty("platformName");
        if (platform == null) {
            platform = ConfigReader.getProperty("platformName");
        }
        System.out.println("Plateforme actuelle: " + platform);

        try {
            if ("Android".equalsIgnoreCase(platform)) {
                return getAndroidDriver();
            } else if ("iOS".equalsIgnoreCase(platform)) {
                return getIOSDriver();
            } else {
                throw new IllegalStateException("Plateforme non supportée: " + platform);
            }
        } catch (Exception e) {
            System.err.println("Erreur getCurrentDriver: " + e.getMessage());
            throw new RuntimeException("Impossible d'initialiser le driver", e);
        }
    }

    public static void closeDriver() {
        try {
            if (iOS != null) {
                iOS.quit();
                iOS = null;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture du driver iOS: " + e.getMessage());
        }

        try {
            if (Android != null) {
                Android.quit();
                Android = null;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture du driver Android: " + e.getMessage());
        }
    }
} 