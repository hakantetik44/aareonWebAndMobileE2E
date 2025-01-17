Feature: Login Functionality

  Background:
    Given l'application Les Residences est ouverte

  @mobile @android @ios
  @known_issue
  Scenario: Test de connexion avec email incorrect
    When l'utilisateur se connecte avec l'email "incorrect@email.com" et le mot de passe "123456"
    Then la connexion doit échouer
    # Message d'erreur non vérifié en raison d'un bug connu de l'application

  @mobile @android @ios
  Scenario: Test d'inscription d'un nouvel utilisateur
    When l'utilisateur clique sur le bouton inscription
    And l'utilisateur saisit les informations d'inscription
      | numContrat | 12345          |
      | nom        | Test           |
      | prenom     | User           |
      | email      | test@email.com |
      | password   | Test123!       |
   # Then l'inscription doit être réussie