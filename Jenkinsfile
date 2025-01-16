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
                    def setupSteps = [
                        'Check Node version': 'node -v',
                        'Check npm version': 'npm -v',
                        'Check current user': 'whoami',
                        'Check current directory': 'pwd',
                        'List global packages': 'npm list -g --depth=0',
                        'Uninstall old Appium': '''
                            npm uninstall -g appium || true
                            npm uninstall -g appium-doctor || true
                            npm cache clean --force
                        ''',
                        'Install Appium': 'npm install -g appium@2.4.1',
                        'Check Appium version': 'appium -v',
                        'Check Appium path': 'which appium',
                        'List current drivers': 'appium driver list || true'
                    ]

                    try {
                        // Execute initial setup steps
                        setupSteps.each { stepName, command ->
                            echo "Executing: ${stepName}"
                            def result = sh(script: command, returnStatus: true)
                            if (result != 0) {
                                error "Failed at step '${stepName}' with exit code ${result}"
                            }
                            echo "${stepName} completed successfully"
                        }

                        // Special handling for uiautomator2 installation
                        echo "Installing uiautomator2 driver with debug logging..."
                        def uiautomator2Result = sh(script: '''
                            export DEBUG=appium*
                            echo "Current PATH: $PATH"
                            echo "Appium location: $(which appium)"
                            echo "Installing uiautomator2 driver..."
                            appium driver install uiautomator2@3.9.8 --source=npm
                        ''', returnStatus: true)

                        if (uiautomator2Result != 0) {
                            error "Failed to install uiautomator2 driver. Exit code: ${uiautomator2Result}"
                        }

                        // Install xcuitest driver if needed
                        if (params.PLATFORM == 'iOS') {
                            echo "Installing xcuitest driver..."
                            def xcuitestResult = sh(script: 'appium driver install xcuitest --source=npm', returnStatus: true)
                            if (xcuitestResult != 0) {
                                error "Failed to install xcuitest driver. Exit code: ${xcuitestResult}"
                            }
                        }

                        // Verify installations
                        echo "Verifying driver installations..."
                        sh 'appium driver list'
                        
                    } catch (Exception e) {
                        echo """
                        ❌ Setup Environment Failed
                        Error: ${e.message}
                        
                        Debugging Information:
                        Node Version: ${sh(script: 'node -v || echo "Not installed"', returnStdout: true).trim()}
                        NPM Version: ${sh(script: 'npm -v || echo "Not installed"', returnStdout: true).trim()}
                        User: ${sh(script: 'whoami', returnStdout: true).trim()}
                        Directory: ${sh(script: 'pwd', returnStdout: true).trim()}
                        Appium Path: ${sh(script: 'which appium || echo "Not found"', returnStdout: true).trim()}
                        
                        Global NPM packages:
                        ${sh(script: 'npm list -g --depth=0 || echo "Failed to list packages"', returnStdout: true).trim()}
                        
                        Appium Drivers:
                        ${sh(script: 'appium driver list || echo "Failed to list drivers"', returnStdout: true).trim()}
                        """
                        
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