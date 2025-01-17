package pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.OS;
import static utils.Driver.getCurrentDriver;
import java.time.Duration;
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

        System.out.println("Saisie de l'email: " + email);
        WebElement emailField = findElement(emailInput);
        emailField.click();
        emailField.clear();
        emailField.sendKeys(email);
        
        System.out.println("Saisie du mot de passe: " + password);
        WebElement passwordField = findElement(passwordInput);
        passwordField.click();
        passwordField.clear();
        passwordField.sendKeys(password);
        
        System.out.println("Clic sur le bouton de connexion");
        findElement(loginButton).click();
    }

    public boolean isErrorMessageDisplayed() {
        By[] errorSelectors = OS.isAndroid() ?
                new By[]{
                    AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Identifiant ou mot de passe incorrect')]"),
                    AppiumBy.xpath("//*[contains(@text, 'erreur')]"),
                    AppiumBy.xpath("//*[contains(@resource-id, 'error')]"),
                    AppiumBy.xpath("//*[contains(@resource-id, 'alert')]")
                } :
                new By[]{
                    AppiumBy.accessibilityId("errorMessage"),
                    AppiumBy.accessibilityId("alertMessage")
                };
                
        WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Duration.ofSeconds(10));
        
        for (By selector : errorSelectors) {
            try {
                if (wait.until(ExpectedConditions.presenceOfElementLocated(selector)).isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
                // Continue to next selector
                continue;
            }
        }
        
        System.out.println("Aucun message d'erreur trouvé avec les sélecteurs disponibles");
        return false;
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

        System.out.println("Saisie du numéro de contrat: " + userInfo.get("numContrat"));
        WebElement contractField = findElement(numContratInput);
        contractField.click();
        contractField.clear();
        contractField.sendKeys(userInfo.get("numContrat"));
        
        System.out.println("Saisie du nom: " + userInfo.get("nom"));
        WebElement nameField = findElement(nomInput);
        nameField.click();
        nameField.clear();
        nameField.sendKeys(userInfo.get("nom"));
        
        System.out.println("Saisie du prénom: " + userInfo.get("prenom"));
        WebElement firstNameField = findElement(prenomInput);
        firstNameField.click();
        firstNameField.clear();
        firstNameField.sendKeys(userInfo.get("prenom"));
        
        System.out.println("Saisie de l'email: " + userInfo.get("email"));
        WebElement emailField = findElement(emailInput);
        emailField.click();
        emailField.clear();
        emailField.sendKeys(userInfo.get("email"));
        
        System.out.println("Saisie du mot de passe: " + userInfo.get("password"));
        WebElement passwordField = findElement(passwordInput);
        passwordField.click();
        passwordField.clear();
        passwordField.sendKeys(userInfo.get("password"));
        
        WebElement confirmPasswordField = findElement(confirmPasswordInput);
        confirmPasswordField.click();
        confirmPasswordField.clear();
        confirmPasswordField.sendKeys(userInfo.get("password"));
    }

    private WebElement findElement(By locator) {
        int maxAttempts = 3;
        int attempt = 0;
        WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Duration.ofSeconds(10));

        while (attempt < maxAttempts) {
            try {
                return wait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (StaleElementReferenceException e) {
                attempt++;
                if (attempt == maxAttempts) {
                    throw e;
                }
            }
        }
        throw new ElementNotInteractableException("L'élément n'est pas devenu cliquable après " + maxAttempts + " tentatives");
    }
} 