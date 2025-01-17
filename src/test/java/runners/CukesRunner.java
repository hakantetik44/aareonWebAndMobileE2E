package runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import utils.ConfigReader;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "json:target/cucumber-reports/cucumber.json",
                "html:target/cucumber-reports/cucumber-reports.html",
                "pretty"
        },
        features = "src/test/resources/features",
        glue = "stepDefinitions",
        dryRun = false,
        tags = "${tags:@all}"
)
public class CukesRunner {
    static {
        String platform = ConfigReader.getProperty("platformName", "android").toLowerCase();
        String tags = "";
        
        switch (platform) {
            case "android":
                tags = "@android or @mobile";
                break;
            case "ios":
                tags = "@ios or @mobile";
                break;
            case "web":
                tags = "@web";
                break;
            default:
                tags = "@all";
        }
        
        System.setProperty("tags", tags);
    }
}