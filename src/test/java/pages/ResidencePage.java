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
                AppiumBy.className("XCUIElementTypeTextField");
        
        By passwordInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[2]") :
                AppiumBy.className("XCUIElementTypeSecureTextField");
        
        By loginButton = OS.isAndroid() ?
                AppiumBy.xpath("//android.widget.Button[@text='CONNEXION']") :
                AppiumBy.xpath("//XCUIElementTypeButton[contains(@name, 'CONNEXION') or contains(@label, 'CONNEXION')]");

        System.out.println("Saisie de l'email: " + email);
        WebElement emailField = findElement(emailInput);
        emailField.click();
        emailField.clear();
        emailField.sendKeys(email);
        hideKeyboard();
        
        System.out.println("Saisie du mot de passe: " + password);
        WebElement passwordField = findElement(passwordInput);
        scrollToElement(passwordField);
        passwordField.click();
        passwordField.clear();
        passwordField.sendKeys(password);
        hideKeyboard();
        
        // Attendre que le clavier soit complètement fermé
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Clic sur le bouton de connexion");
        WebElement button = findElement(loginButton);
        scrollToElement(button);
        button.click();
    }

    private void hideKeyboard() {
        if (OS.isAndroid()) {
            try {
                Thread.sleep(1000); // Wait longer for keyboard animation
                try {
                    ((io.appium.java_client.android.AndroidDriver) getCurrentDriver()).hideKeyboard();
                } catch (Exception e1) {
                    try {
                        // Try pressing back button if hideKeyboard() fails
                        getCurrentDriver().navigate().back();
                    } catch (Exception e2) {
                        System.out.println("Impossible de masquer le clavier via le bouton retour: " + e2.getMessage());
                    }
                }
                Thread.sleep(500); // Wait after hiding keyboard
            } catch (Exception e) {
                System.out.println("Impossible de masquer le clavier: " + e.getMessage());
            }
        } else {
            try {
                // Processus de fermeture du clavier pour iOS
                Thread.sleep(1000);
                try {
                    // Essayer d'abord de cliquer sur le bouton 'Terminé'
                    By doneButton = AppiumBy.xpath("//XCUIElementTypeButton[@name='Done' or @name='Terminé']");
                    getCurrentDriver().findElement(doneButton).click();
                } catch (Exception e1) {
                    try {
                        // Si le bouton 'Terminé' n'est pas trouvé, cliquer sur une zone vide
                        By emptyArea = AppiumBy.xpath("//XCUIElementTypeApplication");
                        getCurrentDriver().findElement(emptyArea).click();
                    } catch (Exception e2) {
                        System.out.println("Impossible de masquer le clavier iOS: " + e2.getMessage());
                    }
                }
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("Erreur lors de la fermeture du clavier iOS: " + e.getMessage());
            }
        }
    }

    private void scrollToElement(WebElement element) {
        if (OS.isAndroid()) {
            try {
                // First try UiScrollable
                String elementText = element.getText();
                if (elementText == null || elementText.isEmpty()) {
                    elementText = element.getAttribute("content-desc");
                }
                
                if (elementText != null && !elementText.isEmpty()) {
                    try {
                        ((io.appium.java_client.android.AndroidDriver) getCurrentDriver())
                            .findElement(AppiumBy.androidUIAutomator(
                                "new UiScrollable(new UiSelector().scrollable(true))" +
                                ".setAsVerticalList().scrollIntoView(" +
                                "new UiSelector().textContains(\"" + elementText + "\"))"
                            ));
                        return;
                    } catch (Exception e) {
                        System.out.println("Premier essai de défilement échoué: " + e.getMessage());
                    }
                }
                
                // If UiScrollable fails, try JavaScript scroll
                String script = "arguments[0].scrollIntoView(true);";
                ((io.appium.java_client.android.AndroidDriver) getCurrentDriver())
                    .executeScript(script, element);
                
                Thread.sleep(500); // Wait for scroll to complete
                
            } catch (Exception e) {
                System.out.println("Impossible de faire défiler jusqu'à l'élément: " + e.getMessage());
            }
        }
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
                    AppiumBy.xpath("//XCUIElementTypeStaticText[contains(@name, 'Identifiant ou mot de passe incorrect')]"),
                    AppiumBy.xpath("//XCUIElementTypeStaticText[contains(@name, 'erreur')]")
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
                AppiumBy.xpath("//XCUIElementTypeButton[contains(@name, 'INSCRIPTION') or contains(@label, 'INSCRIPTION')]");
        getCurrentDriver().findElement(inscriptionButton).click();
    }

    public void fillRegistrationForm(Map<String, String> userInfo) {
        By numContratInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[1]") :
                AppiumBy.className("XCUIElementTypeTextField");
                
        By nomInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[2]") :
                AppiumBy.xpath("(//XCUIElementTypeTextField)[2]");
                
        By prenomInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[3]") :
                AppiumBy.xpath("(//XCUIElementTypeTextField)[3]");
                
        By emailInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[4]") :
                AppiumBy.xpath("(//XCUIElementTypeTextField)[4]");
                
        By passwordInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[5]") :
                AppiumBy.xpath("(//XCUIElementTypeSecureTextField)[1]");

        By confirmPasswordInput = OS.isAndroid() ?
                AppiumBy.xpath("(//android.widget.EditText)[6]") :
                AppiumBy.xpath("(//XCUIElementTypeSecureTextField)[2]");

        System.out.println("Remplissage du formulaire d'inscription...");
        fillField(numContratInput, "numéro de contrat", userInfo.get("numContrat"));
        fillField(nomInput, "nom", userInfo.get("nom"));
        fillField(prenomInput, "prénom", userInfo.get("prenom"));
        fillField(emailInput, "email", userInfo.get("email"));
        fillField(passwordInput, "mot de passe", userInfo.get("password"));
        fillField(confirmPasswordInput, "confirmation du mot de passe", userInfo.get("password"));
        System.out.println("Formulaire d'inscription rempli.");
    }

    private void fillField(By locator, String fieldName, String value) {
        System.out.println("Saisie " + fieldName + ": " + value);
        WebElement field;
        try {
            // Essayer d'abord de trouver l'élément
            field = findElement(locator);
            field.click();
        } catch (Exception e) {
            // Élément non trouvé ou non cliquable, fermer le clavier et réessayer
            System.out.println("Élément non visible, fermeture du clavier et nouvelle tentative...");
            hideKeyboard();
            try {
                Thread.sleep(1000); // Attendre la fermeture du clavier
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            field = findElement(locator);
            field.click();
        }

        field.clear();
        field.sendKeys(value);
        
        // Ne fermer le clavier que si le champ suivant n'est pas visible
        try {
            // Vérifier la visibilité du champ suivant
            getCurrentDriver().findElement(locator).isDisplayed();
        } catch (Exception e) {
            // Si le champ suivant n'est pas visible, fermer le clavier
            hideKeyboard();
        }
        
        try {
            Thread.sleep(500); // Courte pause
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private WebElement findElement(By locator) {
        int maxAttempts = 3;
        int attempt = 0;
        WebDriverWait wait = new WebDriverWait(getCurrentDriver(), Duration.ofSeconds(15));

        while (attempt < maxAttempts) {
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                // Elementi görünür ve tıklanabilir olana kadar bekle
                wait.until(ExpectedConditions.elementToBeClickable(element));
                // Elementin görünür olduğundan emin ol
                if (!element.isDisplayed()) {
                    scrollToElement(element);
                }
                return element;
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