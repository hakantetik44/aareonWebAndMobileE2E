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

    public static WebDriver getCurrentDriver() {
        String platform = System.getProperty("platformName", "android").toLowerCase();
        System.out.println("Getting driver for platform: " + platform);
        
        switch (platform) {
            case "android":
                return getAndroidDriver();
            case "ios":
                return getIOSDriver();
            case "web":
                return getWebDriver();
            default:
                throw new RuntimeException("Platform not supported: " + platform);
        }
    }

    public static WebDriver getWebDriver() {
        if (Web == null) {
            try {
                System.out.println("Starting Web driver...");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--remote-allow-origins=*");
                
                Web = new ChromeDriver(options);
                Web.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                Web.manage().window().maximize();
                System.out.println("Web driver created successfully!");
            } catch (Exception e) {
                System.err.println("Error creating Web driver: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return Web;
    }

    public static AppiumDriver getAndroidDriver() {
        if (Android == null) {
            try {
                System.out.println("Starting Android driver...");
                
                UiAutomator2Options options = new UiAutomator2Options();
                options.setPlatformName("ANDROID");
                options.setAutomationName(ConfigReader.getProperty("androidAutomationName"));
                options.setDeviceName(ConfigReader.getProperty("androidDeviceName"));
                options.setAppPackage(ConfigReader.getProperty("androidAppPackage"));
                options.setAppActivity(ConfigReader.getProperty("androidAppActivity"));
                options.setNoReset(true);
                options.setAutoGrantPermissions(true);
                options.setNewCommandTimeout(Duration.ofSeconds(60));
                options.setAdbExecTimeout(Duration.ofSeconds(60));

                System.out.println("Connecting to Appium...");
                Android = new AppiumDriver(new URL(ConfigReader.getProperty("appiumServerURL")), options);
                Android.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                System.out.println("Android driver created successfully!");

            } catch (Exception e) {
                System.err.println("Error creating Android driver: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return Android;
    }

    public static AppiumDriver getIOSDriver() {
        if (iOS == null) {
            try {
                System.out.println("Starting iOS driver...");
                XCUITestOptions options = new XCUITestOptions();
                options.setPlatformName("iOS")
                        .setAutomationName(ConfigReader.getProperty("iosAutomationName"))
                        .setPlatformVersion(ConfigReader.getProperty("iosPlatformVersion"))
                        .setDeviceName(ConfigReader.getProperty("iosDeviceName"))
                        .setUdid(ConfigReader.getProperty("iosDeviceUDID"))
                        .setBundleId(ConfigReader.getProperty("iosBundleId"))
                        .setNoReset(true)
                        .setAutoAcceptAlerts(true);
                options.setCapability("showXcodeLog", true);
                options.setCapability("wdaLocalPort", 8100);
                options.setCapability("webDriverAgentUrl", "http://192.168.1.4:8100");

                System.out.println("Connecting to Appium...");
                iOS = new AppiumDriver(new URL(ConfigReader.getProperty("appiumServerURL")), options);
                iOS.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                System.out.println("iOS driver created successfully!");

            } catch (Exception e) {
                System.err.println("Error creating iOS driver: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return iOS;
    }

    public static void closeDriver() {
        if (Android != null) {
            Android.quit();
            Android = null;
        }
        if (iOS != null) {
            iOS.quit();
            iOS = null;
        }
        if (Web != null) {
            Web.quit();
            Web = null;
        }
    }
}