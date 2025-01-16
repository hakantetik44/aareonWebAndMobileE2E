<div align="center">

<img src="https://storage.googleapis.com/bkt-ph-prod-homepage-media-public/original_images/aareon-logo.png" width="400px">

# 🏢 Tests E2E Les Residences

[![Tests](https://img.shields.io/badge/Tests-Passing-success?style=for-the-badge&logo=github)](https://github.com/AareonFrance/aareonWebAndMobileE2E)
[![Appium](https://img.shields.io/badge/Appium-2.0-purple?style=for-the-badge&logo=appium)](https://appium.io)
[![Cucumber](https://img.shields.io/badge/Cucumber-BDD-brightgreen?style=for-the-badge&logo=cucumber)](https://cucumber.io)
[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.java.com)
[![Maven](https://img.shields.io/badge/Maven-3.8-red?style=for-the-badge&logo=apache-maven)](https://maven.apache.org)

*Framework de tests automatisés pour l'application mobile Les Residences*

[📱 Documentation](#-à-propos) •
[🚀 Installation](#-installation) •
[📊 Rapports](#-rapports) •
[📞 Contact](#-contact)

---

</div>

## 💫 À propos
Framework de tests end-to-end pour l'application mobile "Les Residences" d'Aareon France. Cette suite de tests automatisés permet de valider le bon fonctionnement de l'application sur les plateformes Android et iOS.

## ⚡ Technologies Utilisées
- 📱 **Appium** : Framework de test mobile multi-plateforme
- 🥒 **Cucumber** : Outil BDD pour les spécifications exécutables
- ☕ **Java** : Langage de programmation principal
- 🎯 **Maven** : Gestionnaire de dépendances et de build
- 🧪 **JUnit** : Framework de test unitaire
- 📊 **Allure** : Génération de rapports de test élégants

## 📋 Prérequis
- ☕ Java JDK 11 ou supérieur
- 🎯 Maven 3.8.x ou supérieur
- 💻 Node.js et npm (pour Appium)
- 📱 Appium Server 2.0 ou supérieur
- 🤖 Android Studio avec SDK (pour les tests Android)
- 🍎 Xcode (pour les tests iOS, macOS uniquement)

## 🚀 Installation

### 1. 📥 Cloner le repository
```bash
git clone https://github.com/AareonFrance/aareonWebAndMobileE2E.git
cd aareonWebAndMobileE2E
```

### 2. 📦 Installer les dépendances
```bash
mvn clean install
```

### 3. ⚙️ Configurer l'environnement
- Copier `configuration.properties.example` vers `configuration.properties`
- Modifier les paramètres selon votre environnement

## 📱 Configuration des Appareils

### 🤖 Android
- Émulateur Android ou appareil physique connecté
- API Level 29+ recommandé
- USB Debugging activé (pour les appareils physiques)

### 🍎 iOS
- Simulateur iOS ou appareil physique
- iOS 14+ recommandé
- XCode 12+ installé
- Certificats de développement configurés

## ▶️ Exécution des Tests

### 🎯 Tous les tests
```bash
mvn clean test
```

### 🏷️ Tests spécifiques par tag
```bash
mvn test -Dcucumber.filter.tags="@login"
```

### 📱 Tests par plateforme
```bash
# Android uniquement
mvn test -DplatformName=Android

# iOS uniquement
mvn test -DplatformName=iOS
```

## 📊 Rapports
Les rapports de test sont générés automatiquement après chaque exécution :
- 📈 **Allure** : `target/allure-results`
- 📑 **Cucumber** : `target/cucumber-reports`

Pour visualiser le rapport Allure :
```bash
allure serve target/allure-results
```

## 🌍 UTM Tracking
- 🏢 **Source** : aareon_france
- 💻 **Medium** : github
- 📱 **Campaign** : mobile_testing
- 🔄 **Content** : e2e_framework

## 🤝 Contribution
1. 🔀 Fork le projet
2. 🌿 Créer une branche (`git checkout -b feature/AmazingFeature`)
3. ✍️ Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. 📤 Push vers la branche (`git push origin feature/AmazingFeature`)
5. 🔍 Ouvrir une Pull Request

## 📜 License
Copyright © 2025 [Aareon France](https://www.aareon.fr/). Tous droits réservés.

## 📞 Contact
- 🌐 **Site Web** : [www.aareon.fr](https://www.aareon.fr)
- 📧 **Email** : contact@aareon.fr
- 💼 **LinkedIn** : [Aareon France](https://www.linkedin.com/company/aareon-france)

<div align="center">

---

<img src="https://storage.googleapis.com/bkt-ph-prod-homepage-media-public/original_images/aareon-logo.png" width="200px">

*Développé avec ❤️ par l'équipe QA d'Aareon France*

[![GitHub stars](https://img.shields.io/github/stars/AareonFrance/aareonWebAndMobileE2E?style=social)](https://github.com/AareonFrance/aareonWebAndMobileE2E/stargazers)
[![Twitter Follow](https://img.shields.io/twitter/follow/AareonFrance?style=social)](https://twitter.com/AareonFrance)

</div>
