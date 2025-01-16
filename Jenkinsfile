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
                        // Önce mevcut kurulumları temizle
                        sh '''
                            echo "Mevcut kurulumları temizleme..."
                            npm uninstall -g appium || true
                            npm uninstall -g appium-doctor || true
                            rm -rf ~/.appium || true
                            rm -rf ~/.npm/_cacache || true
                            npm cache clean -f
                        '''

                        // Node ve npm versiyonlarını kontrol et
                        sh '''
                            echo "Node ve npm versiyonları:"
                            node -v
                            npm -v
                        '''

                        // Appium'u kur
                        sh '''
                            echo "Appium kurulumu yapılıyor..."
                            npm install -g appium@2.5.4
                            appium -v
                        '''

                        if (params.PLATFORM == 'Android') {
                            // Android için uiautomator2 sürücüsünü kur
                            sh '''
                                echo "uiautomator2 sürücüsü kuruluyor..."
                                appium driver uninstall uiautomator2 || true
                                appium driver install uiautomator2@3.9.8
                                echo "Kurulu sürücüler:"
                                appium driver list
                            '''
                        } else if (params.PLATFORM == 'iOS') {
                            // iOS için xcuitest sürücüsünü kur
                            sh '''
                                echo "xcuitest sürücüsü kuruluyor..."
                                appium driver uninstall xcuitest || true
                                appium driver install xcuitest
                                echo "Kurulu sürücüler:"
                                appium driver list
                            '''
                        }

                        // Kurulum sonrası kontrol
                        sh '''
                            echo "Kurulum sonrası durum:"
                            echo "Appium versiyonu:"
                            appium -v
                            echo "Kurulu sürücüler:"
                            appium driver list
                        '''
                        
                    } catch (Exception e) {
                        echo """
                        ❌ Kurulum Hatası
                        Hata: ${e.message}
                        
                        Sistem Bilgileri:
                        Node: ${sh(script: 'node -v || echo "Kurulu değil"', returnStdout: true).trim()}
                        NPM: ${sh(script: 'npm -v || echo "Kurulu değil"', returnStdout: true).trim()}
                        Kullanıcı: ${sh(script: 'whoami', returnStdout: true).trim()}
                        Dizin: ${sh(script: 'pwd', returnStdout: true).trim()}
                        """
                        
                        currentBuild.result = 'FAILURE'
                        error "Kurulum başarısız: ${e.message}"
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
                sh 'pkill -f appium || true'
                
                // Artifact'ları arşivle
                archiveArtifacts artifacts: '**/target/', allowEmptyArchive: true
                
                // Eski Allure raporlarını temizle
                sh '''
                    rm -rf allure-report || true
                    rm -rf allure-results || true
                    rm -f allure-report.zip || true
                '''
                
                // Allure raporu oluştur
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
                
                // Cucumber raporu oluştur
                cucumber([
                    buildStatus: 'UNSTABLE',
                    reportTitle: 'Cucumber Report',
                    fileIncludePattern: '**/*.json',
                    trendsLimit: 10,
                    classifications: [
                        [
                            'key': 'Platform',
                            'value': params.PLATFORM
                        ],
                        [
                            'key': 'Branch',
                            'value': env.BRANCH_NAME
                        ]
                    ]
                ])
                
                // Workspace'i temizle
                cleanWs()
                
                // Test sonuçlarını göster
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