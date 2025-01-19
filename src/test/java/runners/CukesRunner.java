package runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm;
import org.junit.runner.RunWith;
import utils.ConfigReader;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "json:target/cucumber.json",
                "html:target/cucumber-reports/cucumber-reports.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "pretty",
                "rerun:target/rerun.txt",
                "timeline:target/timeline-reports",
                "json:target/cucumber-reports/CucumberTestReport.json"
        },
        features = "src/test/resources/features",
        glue = "stepDefinitions",
        dryRun = false,
        monochrome = true
)
public class CukesRunner {
    static {
        String platform = System.getProperty("platformName", ConfigReader.getProperty("platformName", "android")).toLowerCase();
        String tags = "";
        
        // Set platform for Allure environment
        System.setProperty("allure.environment.platform", platform.toUpperCase());
        
        switch (platform) {
            case "android":
                tags = "@android";
                break;
            case "ios":
                tags = "@ios";
                break;
            case "web":
                tags = "@web";
                break;
            default:
                tags = "@all";
        }
        
        System.setProperty("cucumber.filter.tags", tags);
    }
}