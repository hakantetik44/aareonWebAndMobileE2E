package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.net.URL;
import java.time.Duration;

public class Driver {
    private Driver() {
    }

    public static AppiumDriver Android;
    public static AppiumDriver iOS;
    public static WebDriver Web;

    public static WebDriver getWebDriver() {
        if (Web == null) {
            try {
                System.out.println("Démarrage du driver Web...");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--remote-allow-origins=*");
                
                Web = new ChromeDriver(options);
                Web.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                Web.manage().window().maximize();
                System.out.println("Driver Web créé avec succès!");
            } catch (Exception e) {
                System.err.println("Erreur lors de la création du driver Web: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return Web;
    }

    public static AppiumDriver getAndroidDriver() {
        if (Android == null) {
            try {
                System.out.println("Démarrage du driver Android...");
                
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

                System.out.println("Connexion à Appium...");
                Android = new AppiumDriver(new URL("http://127.0.0.1:4723"), options);
                Android.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                System.out.println("Driver Android créé avec succès!");

            } catch (Exception e) {
                System.err.println("Erreur lors de la création du driver Android: " + e.getMessage());
                e.printStackTrace();
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
            }
        }
        return iOS;
    }

    public static WebDriver getCurrentDriver() {
        try {
            if (OS.isAndroid()) {
                return getAndroidDriver();
            } else if (OS.isIOS()) {
                return getIOSDriver();
            } else if (OS.isWeb()) {
                return getWebDriver();
            } else {
                throw new IllegalStateException("Système d'exploitation non supporté: " + OS.OS);
            }
        } catch (Exception e) {
            System.err.println("Erreur getCurrentDriver: " + e.getMessage());
            return null;
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

        try {
            if (Web != null) {
                Web.quit();
                Web = null;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture du driver Web: " + e.getMessage());
        }
    }
}