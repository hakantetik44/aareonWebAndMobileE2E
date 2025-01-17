pipeline {
    agent any

    environment {
        ANDROID_HOME = '/Users/hakantetik/Library/Android/sdk'
        PATH = "${env.ANDROID_HOME}/platform-tools:${env.ANDROID_HOME}/tools:${env.PATH}"
        ALLURE_HOME = tool 'Allure'
    }

    tools {
        maven 'maven'
        jdk 'JDK17'
        nodejs 'Node'
        allure 'Allure'
    }

    parameters {
        choice(
            name: 'PLATFORM',
            choices: ['Android', 'iOS', 'Web'],
            description: 'Sélectionnez la plateforme de test'
        )
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    sh '''
                        echo "🔧 Informations sur l'environnement:"
                        echo "ANDROID_HOME: $ANDROID_HOME"
                        echo "PATH: $PATH"
                        echo "JAVA_HOME: $JAVA_HOME"
                    '''
                }
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    try {
                        if (params.PLATFORM != 'Web') {
                            sh '''
                                echo "📱 Installation d'Appium"
                                npm uninstall -g appium || true
                                npm install -g appium@2.5.4
                                
                                echo "🔍 Vérification du Driver"
                                INSTALLED_DRIVERS=$(appium driver list --installed || true)
                                echo "Drivers installés:"
                                echo "$INSTALLED_DRIVERS"
                                
                                if [ "${PLATFORM}" = "Android" ]; then
                                    echo "🤖 Gestion du Driver Android"
                                    if echo "$INSTALLED_DRIVERS" | grep -q "uiautomator2"; then
                                        echo "Mise à jour du driver uiautomator2..."
                                        appium driver update uiautomator2 || true
                                    else
                                        echo "uiautomator2 driver installé..."
                                        appium driver install uiautomator2 || true
                                    fi
                                elif [ "${PLATFORM}" = "iOS" ]; then
                                    echo "🍎 Gestion du Driver iOS"
                                    if echo "$INSTALLED_DRIVERS" | grep -q "xcuitest"; then
                                        echo "Mise à jour du driver xcuitest..."
                                        appium driver update xcuitest || true
                                    else
                                        echo "xcuitest driver installé..."
                                        appium driver install xcuitest || true
                                    fi
                                fi
                                
                                echo "✅ Installation Terminée"
                                echo "État final:"
                                appium driver list --installed
                            '''
                        }
                    } catch (Exception e) {
                        echo "❌ Erreur d'Installation: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('Start Appium') {
            when {
                expression { params.PLATFORM != 'Web' }
            }
            steps {
                script {
                    try {
                        sh '''
                            echo "🚀 Démarrage d'Appium..."
                            pkill -f appium || true
                            sleep 2
                            
                            echo "Démarrage du serveur Appium..."
                            appium --log appium.log --relaxed-security > /dev/null 2>&1 &
                            
                            echo "Attente du démarrage du serveur..."
                            sleep 10
                            
                            echo "État du serveur..."
                            if curl -s http://localhost:4723/status | grep -q "ready"; then
                                echo "✅ Serveur Appium démarré avec succès"
                            else
                                echo "❌ Échec du démarrage du serveur Appium"
                                cat appium.log
                                exit 1
                            fi
                            
                            if [ "${PLATFORM}" = "Android" ]; then
                                echo "📱 Vérification de l'Appareil Android"
                                adb devices
                                
                                if ! adb devices | grep -q "device$"; then
                                    echo "❌ Aucun appareil connecté!"
                                    exit 1
                                fi
                                echo "✅ Connexion à l'appareil Android réussie"
                            fi
                        '''
                    } catch (Exception e) {
                        echo "❌ Erreur de Démarrage Appium: ${e.message}"
                        sh 'cat appium.log || true'
                        throw e
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        def platformTag = params.PLATFORM.toLowerCase()
                        sh """
                            echo "📂 Création des Répertoires de Test..."
                            rm -rf target/cucumber-reports target/allure-results || true
                            mkdir -p target/cucumber-reports
                            mkdir -p target/allure-results

                            echo "🧪 Démarrage des Tests..."
                            mvn clean test -DplatformName=${params.PLATFORM} -Dcucumber.filter.tags="@${platformTag}" -Dcucumber.execution.strict=false
                        """
                    } catch (Exception e) {
                        echo """
                            ⚠️ Résultats des Tests
                            État: Certains tests ont échoué
                            Plateforme: ${params.PLATFORM}
                            Build: ${BUILD_NUMBER}
                            Note: Les problèmes connus sont signalés comme des avertissements
                        """
                        // Don't fail the build for known issues
                        if (e.message.contains('@known_issue')) {
                            currentBuild.result = 'UNSTABLE'
                        } else {
                            throw e
                        }
                    }
                }
            }
            options {
                timeout(time: 30, unit: 'MINUTES')
                retry(2)
            }
        }
    }

    post {
        always {
            script {
                sh 'pkill -f appium || true'
                
                cucumber(
                    fileIncludePattern: '**/cucumber.json',
                    jsonReportDirectory: 'target/cucumber-reports',
                    reportTitle: 'Résultats des Tests',
                    buildStatus: currentBuild.result == 'UNSTABLE' ? 'UNSTABLE' : 'FAILURE',
                    skipFailedTests: true,
                    classificationsFilePattern: '**/classifications.properties',
                    mergeFeaturesById: true,
                    mergeFeaturesWithRetest: true
                )
                
                // Allure rapor dizinini temizle
                sh 'rm -rf target/allure-report || true'
                
                // Allure raporu oluştur
                allure([
                    includeProperties: true,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']],
                    report: 'target/allure-report'
                ])

                // Allure komut satırı ile raporu yeniden oluştur
                sh """
                    export PATH="${env.ALLURE_HOME}/bin:${env.PATH}"
                    allure generate target/allure-results --clean -o target/allure-report
                """

                // Allure raporlarını arşivle
                archiveArtifacts artifacts: 'target/allure-results/**/*.*,target/allure-report/**/*.*', fingerprint: true

                echo """
                    📊 Résultats des Tests:
                    📱 Plateforme: ${params.PLATFORM}
                    🌿 Branche: ${env.BRANCH_NAME ?: 'unknown'}
                    🏗️ État: ${currentBuild.currentResult}
                    ℹ️ Note: Les tests marqués @known_issue sont signalés comme des avertissements
                """
            }
        }
        cleanup {
            cleanWs()
        }
    }
}