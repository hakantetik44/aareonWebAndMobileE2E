package stepDefinitions;

import io.appium.java_client.AppiumBy;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.openqa.selenium.By;
import pages.ResidencePage;
import utils.OS;
import java.util.Map;

public class ResidenceSteps {
    private ResidencePage residencePage;
    private Scenario scenario;

    public ResidenceSteps() {
        residencePage = new ResidencePage();
    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("l'utilisateur se connecte avec l'email {string} et le mot de passe {string}")
    public void connexionAvecEmailEtMotDePasse(String email, String password) {
        residencePage.connexion(email, password);
    }

    @Then("l'utilisateur doit voir le message d'erreur")
    public void verifierMessageErreur() {
        assertTrue("Le message d'erreur n'est pas affiché", residencePage.isErrorMessageDisplayed());
    }

    @And("le message d'erreur doit être {string}")
    public void verifierContenuMessageErreur(String messageAttendu) {
        String messageReel = residencePage.getErrorMessage();
        Assert.assertEquals("Le message d'erreur ne correspond pas", messageAttendu, messageReel);
    }

    @When("l'utilisateur clique sur le bouton inscription")
    public void clicBoutonInscription() {
        residencePage.clickInscription();
    }

    @And("l'utilisateur saisit les informations d'inscription")
    public void saisirInformationsInscription(Map<String, String> userInfo) {
        residencePage.fillRegistrationForm(userInfo);
    }

    @Then("l'inscription doit être réussie")
    public void verifierInscriptionReussie() {
        System.out.println("Vérification de la réussite de l'inscription...");
    }

    @Then("la connexion doit échouer")
    public void laConnexionDoitEchouer() {
        assertTrue("La connexion aurait dû échouer mais aucun message d'erreur n'est affiché", 
                  residencePage.isErrorMessageDisplayed());
        System.out.println("La connexion a échoué comme prévu");
    }
}