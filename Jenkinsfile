pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'JDK17'
        nodejs 'Node'
    }

    parameters {
        choice(
            name: 'PLATFORM',
            choices: ['Android', 'iOS', 'Web'],
            description: 'Test platformunu seçin'
        )
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout()
    }

    stages {
        stage('Initialize') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Setup Environment') {
            when {
                expression { params.PLATFORM != 'Web' }
            }
            steps {
                script {
                    try {
                        // Node.js ve npm versiyonlarını kontrol et
                        sh '''
                            set -x  # Enable command echo
                            echo "Node version check:"
                            node -v || echo "Node.js is not installed or not in PATH"
                            
                            echo "NPM version check:"
                            npm -v || echo "npm is not installed or not in PATH"
                            
                            echo "Current user and permissions:"
                            whoami
                            echo "Current directory:"
                            pwd
                            
                            echo "Removing existing Appium installations..."
                            npm list -g appium || true
                            npm uninstall -g appium || true
                            npm uninstall -g appium-inspector || true
                            npm uninstall -g appium-xcuitest-driver || true
                            
                            echo "Installing Appium..."
                            npm install -g appium@2.0.0 || { echo "Failed to install Appium"; exit 1; }
                            
                            echo "Verifying Appium installation..."
                            appium -v || { echo "Appium installation verification failed"; exit 1; }
                            
                            echo "Installing Appium drivers..."
                            appium driver list || true
                            appium driver install uiautomator2 || { echo "Failed to install uiautomator2 driver"; exit 1; }
                            appium driver install xcuitest || { echo "Failed to install xcuitest driver"; exit 1; }
                            
                            echo "Verifying installed drivers:"
                            appium driver list
                        '''
                    } catch (Exception e) {
                        echo "Setup Environment stage failed with error: ${e.message}"
                        sh 'npm list -g || true'  // List global packages
                        currentBuild.result = 'FAILURE'
                        error "Setup Environment stage failed: ${e.message}"
                    }
                }
            }
        }

        stage('Start Appium Server') {
            when {
                expression { params.PLATFORM != 'Web' }
            }
            steps {
                script {
                    try {
                        sh '''
                            echo "Cleaning up existing Appium processes..."
                            pkill -f appium || true
                            sleep 5
                            
                            echo "Starting Appium server..."
                            appium --allow-insecure chromedriver_autodownload -p 4723 --log-level debug --relaxed-security > appium.log 2>&1 &
                            
                            echo "Waiting for server to start..."
                            sleep 30
                            
                            echo "Checking server status..."
                            if curl -s http://localhost:4723/status; then
                                echo "Appium server is running successfully"
                            else
                                echo "Appium server failed to start"
                                echo "Appium logs:"
                                cat appium.log
                                exit 1
                            fi
                        '''
                    } catch (Exception e) {
                        echo "Start Appium Server stage failed: ${e.message}"
                        sh 'cat appium.log || true'
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        sh """
                            echo "Starting test execution..."
                            mvn clean test -Dcucumber.filter.tags="@smoke" \
                            -DplatformName=${params.PLATFORM} \
                            -Dappium.server.url=http://localhost:4723 \
                            -Dmaven.test.failure.ignore=true \
                            -Dcucumber.plugin="json:target/cucumber-reports/cucumber.json" \
                            -Dcucumber.plugin="html:target/cucumber-reports/cucumber.html" \
                            -Dcucumber.plugin="pretty" \
                            -Dcucumber.plugin="io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
                        """
                    } catch (Exception e) {
                        echo "Test execution failed: ${e.message}"
                        sh 'cat appium.log || true'
                        currentBuild.result = 'UNSTABLE'
                        // Don't throw the exception here to allow report generation
                    }
                }
            }
        }

        stage('Generate Reports') {
            steps {
                script {
                    try {
                        allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'target/allure-results']]
                        ])
                    } catch (Exception e) {
                        echo "Report generation failed: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // Appium server'ı durdur
                if (params.PLATFORM != 'Web') {
                    sh 'pkill -f appium || true'
                }
                
                // Test raporlarını arşivle
                archiveArtifacts artifacts: '**/target/**/*', allowEmptyArchive: true
                
                // Allure raporu oluştur
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
                
                // Cucumber raporu oluştur
                cucumber buildStatus: 'UNSTABLE',
                        failedFeaturesNumber: -1,
                        failedScenariosNumber: -1,
                        skippedStepsNumber: -1,
                        failedStepsNumber: -1,
                        classifications: [
                            [key: 'Platform', value: params.PLATFORM],
                            [key: 'Branch', value: env.BRANCH_NAME]
                        ]
                
                // Workspace'i temizle
                cleanWs()
                
                // Test sonuçlarını yazdır
                echo """
                ❌ Test Sonuçları:
                📱 Platform: ${params.PLATFORM}
                🌿 Branch: ${env.BRANCH_NAME}
                ⚠️ Status: ${currentBuild.result}
                """
            }
        }
    }
}