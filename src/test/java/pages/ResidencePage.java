package pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.OS;
import static utils.Driver.getCurrentDriver;
import java.util.Map;

public class ResidencePage extends BasePage {

    public ResidencePage() {
        super(getCurrentDriver());
    }

    public void connexion(String email, String password) {
        By emailInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[1]") :
                AppiumBy.accessibilityId("emailInput");
        
        By passwordInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[2]") :
                AppiumBy.accessibilityId("passwordInput");
        
        By loginButton = OS.isAndroid() ?
                AppiumBy.xpath("//android.widget.Button") :
                AppiumBy.accessibilityId("loginButton");

        System.out.println("Email giriliyor: " + email);
        getCurrentDriver().findElement(emailInput).sendKeys(email);
        
        System.out.println("Şifre giriliyor: " + password);
        getCurrentDriver().findElement(passwordInput).sendKeys(password);
        
        System.out.println("Login butonuna tıklanıyor");
        getCurrentDriver().findElement(loginButton).click();
    }

    public boolean isErrorMessageDisplayed() {
        By errorMessage = OS.isAndroid() ?
                AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Identifiant ou mot de passe incorrect')]") :
                AppiumBy.accessibilityId("errorMessage");
        try {
            return getCurrentDriver().findElement(errorMessage).isDisplayed();
        } catch (Exception e) {
            System.out.println("Message d'erreur non trouvé: " + e.getMessage());
            return false;
        }
    }

    public String getErrorMessage() {
        By errorMessage = OS.isAndroid() ?
                AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Identifiant ou mot de passe incorrect')]") :
                AppiumBy.accessibilityId("errorMessage");
        try {
            return getCurrentDriver().findElement(errorMessage).getText();
        } catch (Exception e) {
            System.out.println("Message d'erreur non trouvé: " + e.getMessage());
            return "";
        }
    }

    public void clickProfile() {
        By profileButton = OS.isAndroid() ?
                AppiumBy.xpath("//android.widget.TextView[@text='Mon Profil']") :
                AppiumBy.accessibilityId("profileButton");
        getCurrentDriver().findElement(profileButton).click();
    }

    public void clickDocuments() {
        By documentsButton = OS.isAndroid() ?
                AppiumBy.xpath("//android.widget.TextView[@text='Mes Documents']") :
                AppiumBy.accessibilityId("documentsButton");
        getCurrentDriver().findElement(documentsButton).click();
    }

    public void clickInvoices() {
        By invoicesButton = OS.isAndroid() ?
                AppiumBy.xpath("//android.widget.TextView[@text='Mes Factures']") :
                AppiumBy.accessibilityId("invoicesButton");
        getCurrentDriver().findElement(invoicesButton).click();
    }

    public void clickInscription() {
        By inscriptionButton = OS.isAndroid() ?
                AppiumBy.xpath("//android.widget.Button[@text='INSCRIPTION']") :
                AppiumBy.accessibilityId("inscriptionButton");
        getCurrentDriver().findElement(inscriptionButton).click();
    }

    public void fillRegistrationForm(Map<String, String> userInfo) {
        By numContratInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[1]") :
                AppiumBy.accessibilityId("numContratInput");
                
        By nomInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[2]") :
                AppiumBy.accessibilityId("nomInput");
                
        By prenomInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[3]") :
                AppiumBy.accessibilityId("prenomInput");
                
        By emailInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[4]") :
                AppiumBy.accessibilityId("emailInput");
                
        By passwordInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[5]") :
                AppiumBy.accessibilityId("passwordInput");

        By confirmPasswordInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[6]") :
                AppiumBy.accessibilityId("confirmPasswordInput");

        System.out.println("Numara giriliyor: " + userInfo.get("numContrat"));
        getCurrentDriver().findElement(numContratInput).sendKeys(userInfo.get("numContrat"));
        
        System.out.println("İsim giriliyor: " + userInfo.get("nom"));
        getCurrentDriver().findElement(nomInput).sendKeys(userInfo.get("nom"));
        
        System.out.println("Soyisim giriliyor: " + userInfo.get("prenom"));
        getCurrentDriver().findElement(prenomInput).sendKeys(userInfo.get("prenom"));
        
        System.out.println("Email giriliyor: " + userInfo.get("email"));
        getCurrentDriver().findElement(emailInput).sendKeys(userInfo.get("email"));
        
        System.out.println("Şifre giriliyor: " + userInfo.get("password"));
        getCurrentDriver().findElement(passwordInput).sendKeys(userInfo.get("password"));
        getCurrentDriver().findElement(confirmPasswordInput).sendKeys(userInfo.get("password"));
    }
} 