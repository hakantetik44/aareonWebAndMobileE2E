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
                    // Node.js ve npm kurulumunu kontrol et
                    sh '''
                        node -v
                        npm -v
                    '''
                    
                    // Appium ve gerekli driver'ları kur
                    sh '''
                        npm install -g appium@2.0.0
                        appium driver install uiautomator2
                        appium driver install xcuitest
                    '''
                }
            }
        }

        stage('Start Appium Server') {
            when {
                expression { params.PLATFORM != 'Web' }
            }
            steps {
                script {
                    sh '''
                        # Önceki Appium instance'larını temizle
                        pkill -f appium || true
                        
                        # Appium server'ı başlat
                        appium --allow-insecure chromedriver_autodownload -p 4723 > appium.log 2>&1 &
                        
                        # Server'ın başlamasını bekle
                        sleep 15
                        
                        # Server'ın çalıştığını kontrol et
                        if ! curl -s http://localhost:4723/status > /dev/null; then
                            echo "Appium server başlatılamadı!"
                            exit 1
                        fi
                        
                        echo "Appium server başarıyla başlatıldı"
                    '''
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        sh """
                            # Test çalıştırma
                            mvn clean test \
                            -DplatformName=${params.PLATFORM} \
                            -Dappium.server.url=http://localhost:4723
                        """
                    } catch (Exception e) {
                        // Test loglarını kaydet
                        sh 'cat appium.log || true'
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
                        cp appium.log test-reports/ || true
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
                'target/cucumber-reports/**/*',
                'appium.log'
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
                classifications: [
                    [key: 'Platform', value: params.PLATFORM],
                    [key: 'Branch', value: env.BRANCH_NAME]
                ]
            )

            // Workspace temizle
            cleanWs()
        }
        
        success {
            echo '''
              ✅ Test Sonuçları:
              📱 Platform: ${params.PLATFORM}
              🌿 Branch: ${env.BRANCH_NAME}
              ✨ Status: Başarılı
              '''
        }
        
        failure {
            echo '''
              ❌ Test Sonuçları:
              📱 Platform: ${params.PLATFORM}
              🌿 Branch: ${env.BRANCH_NAME}
              ⚠️ Status: Başarısız
              '''
        }
    }
}