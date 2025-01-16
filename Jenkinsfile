pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'JDK17'
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

        stage('Start Appium Server') {
            when {
                expression { params.PLATFORM != 'Web' }
            }
            steps {
                script {
                    sh '''
                        npm install -g appium
                        appium -p 4723 &
                        sleep 10
                    '''
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        sh """
                            mvn clean test \
                            -DplatformName=${params.PLATFORM}
                        """
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('Generate Reports') {
            steps {
                script {
                    sh """
                        mkdir -p test-reports
                        cp -r target/cucumber-reports/* test-reports/ || true
                        cp -r target/surefire-reports test-reports/ || true
                        cp -r target/allure-results test-reports/ || true
                        zip -r test-reports.zip test-reports/
                    """
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
            }

            // Test raporlarını arşivle
            archiveArtifacts artifacts: [
                'test-reports.zip',
                'target/cucumber-reports/**/*'
            ].join(', '), fingerprint: true
            
            // Allure raporu
            allure([
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])

            // Cucumber raporu
            cucumber(
                buildStatus: 'UNSTABLE',
                fileIncludePattern: '**/cucumber.json',
                jsonReportDirectory: 'target/cucumber-reports',
                reportTitle: 'Cucumber Test Raporu',
                classifications: [
                    ['key': 'Platform', 'value': params.PLATFORM],
                    ['key': 'Branch', 'value': env.BRANCH_NAME]
                ]
            )

            cleanWs()
        }

        success {
            echo """
            ✅ Test Sonuçları:
            📱 Platform: ${params.PLATFORM}
            🌿 Branch: ${env.BRANCH_NAME}
            ✨ Status: Başarılı
            """
        }

        failure {
            echo """
            ❌ Test Sonuçları:
            📱 Platform: ${params.PLATFORM}
            🌿 Branch: ${env.BRANCH_NAME}
            ⚠️ Status: Başarısız
            """
        }
    }
} 