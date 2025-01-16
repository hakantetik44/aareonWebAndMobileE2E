<div align="center">

<img src="https://storage.googleapis.com/bkt-ph-prod-homepage-media-public/original_images/aareon-logo.png" width="400px">

# 🏢 Tests E2E Web & Mobile Les Residences

[![Tests](https://img.shields.io/badge/Tests-Passing-success?style=for-the-badge&logo=github)](https://github.com/hakantetik44/aareonWebAndMobileE2E)
[![Selenium](https://img.shields.io/badge/Selenium-4.0-green?style=for-the-badge&logo=selenium)](https://www.selenium.dev)
[![Appium](https://img.shields.io/badge/Appium-2.0-purple?style=for-the-badge&logo=appium)](https://appium.io)
[![Cucumber](https://img.shields.io/badge/Cucumber-BDD-brightgreen?style=for-the-badge&logo=cucumber)](https://cucumber.io)
[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.java.com)
[![Maven](https://img.shields.io/badge/Maven-3.8-red?style=for-the-badge&logo=apache-maven)](https://maven.apache.org)

*Framework de tests automatisés pour l'application web et mobile Les Residences*

[📱 Documentation](#-à-propos) •
[🚀 Installation](#-installation) •
[📊 Rapports](#-rapports) •
[📞 Contact](#-contact)

---

</div>

## 💫 À propos
Framework de tests end-to-end pour l'application "Les Residences" d'Aareon France. Cette suite de tests automatisés permet de valider le bon fonctionnement de :
- 🌐 L'application web responsive
- 📱 L'application mobile Android et iOS
- 🔄 La synchronisation des données entre les plateformes

## ⚡ Technologies Utilisées
- 🌐 **Selenium** : Tests automatisés web
- 📱 **Appium** : Tests automatisés mobile
- 🥒 **Cucumber** : Spécifications BDD
- ☕ **Java** : Langage de programmation
- 🎯 **Maven** : Gestion des dépendances
- 🧪 **JUnit** : Framework de test
- 📊 **Allure** : Rapports de test

## 📋 Prérequis

### 🌐 Tests Web
- ☕ Java JDK 11+
- 🎯 Maven 3.8.x+
- 🌐 Navigateurs :
  - Chrome
  - Firefox
  - Safari
  - Edge

### 📱 Tests Mobile
- 💻 Node.js et npm
- 📱 Appium 2.0+
- 🤖 Android Studio & SDK
- 🍎 Xcode (pour iOS)

## 🚀 Installation

### 1. 📥 Cloner le repository
```bash
git clone https://github.com/hakantetik44/aareonWebAndMobileE2E.git
cd aareonWebAndMobileE2E
```

### 2. 📦 Installer les dépendances
```bash
mvn clean install
```

### 3. ⚙️ Configuration

#### 🌐 Web
```properties
webUrl=https://lesresidences-et-moi.com
browser=chrome
```

#### 📱 Mobile
```properties
# Android
androidAppPackage=fr.aareon.lesresidences.bis
androidAppActivity=fr.aareon.lesresidences.bis.MainActivity
deviceName=emulator-5554

# iOS
iosBundleId=fr.aareon.lesresidences.bis
iosDeviceName=iPhone 14
```

## ▶️ Exécution des Tests

### 🎯 Tous les tests
```bash
mvn clean test
```

### 🌐 Tests Web uniquement
```bash
mvn test -Dplatform=web
```

### 📱 Tests Mobile par plateforme
```bash
# Android
mvn test -Dplatform=android

# iOS
mvn test -Dplatform=ios
```

### 🏷️ Tests par fonctionnalité
```bash
mvn test -Dcucumber.filter.tags="@login or @registration"
```

## 📊 Rapports
Les rapports détaillés sont générés pour chaque plateforme :
- 📈 **Allure** : `target/allure-results`
  - Vue d'ensemble des tests
  - Screenshots des erreurs
  - Temps d'exécution
  - Métriques de qualité
- 📑 **Cucumber** : `target/cucumber-reports`
  - Rapports HTML
  - Rapports JSON
  - Rapports XML

Pour visualiser les rapports :
```bash
# Allure
allure serve target/allure-results

# Cucumber (ouvre le rapport HTML)
open target/cucumber-reports/index.html
```

## 🔄 Intégration Continue (CI/CD)

### Jenkins Pipeline

Le projet utilise Jenkins pour l'intégration et le déploiement continus. Le `Jenkinsfile` définit plusieurs stages :

```groovy
// Extrait du Jenkinsfile
pipeline {
    agent any
    stages {
        stage('Build') { ... }
        stage('Test') { ... }
        stage('Report') { ... }
    }
}
```

### 🔧 Configuration Jenkins

1. **Prérequis Jenkins**
   - Jenkins 2.375+ avec Pipeline plugin
   - Plugins nécessaires :
     - Maven Integration
     - Cucumber Reports
     - Allure Jenkins Plugin
     - Android SDK
     - Xcode Integration

2. **Variables d'Environnement**
   ```groovy
   environment {
       JAVA_HOME = '/usr/lib/jvm/java-11-openjdk'
       ANDROID_HOME = '/opt/android-sdk'
       XCODE_PATH = '/Applications/Xcode.app'
   }
   ```

3. **Déclencheurs**
   - Push sur la branche main
   - Pull Requests
   - Planification quotidienne (nightly builds)

### 📊 Rapports Jenkins

Le pipeline génère automatiquement :
- Rapports Cucumber dans Jenkins
- Tableaux de bord Allure
- Couverture de code
- Temps d'exécution des tests

### 🔔 Notifications

Configuration des notifications pour :
- Slack
- Email
- Microsoft Teams

Pour plus de détails, consultez le [Jenkinsfile](./Jenkinsfile) du projet.

## 🌍 UTM Tracking
- 🏢 **Source** : aareon_france
- 💻 **Medium** : github
- 📱 **Campaign** : web_mobile_testing
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

[![GitHub stars](https://img.shields.io/github/stars/hakantetik44/aareonWebAndMobileE2E?style=social)](https://github.com/hakantetik44/aareonWebAndMobileE2E/stargazers)
[![Twitter Follow](https://img.shields.io/twitter/follow/AareonFrance?style=social)](https://twitter.com/AareonFrance)

</div>
