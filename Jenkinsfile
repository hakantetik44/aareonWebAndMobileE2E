pipeline {
    agent any

    environment {
        ANDROID_HOME = '/Users/hakantetik/Library/Android/sdk'
        PATH = "${env.ANDROID_HOME}/platform-tools:${env.ANDROID_HOME}/tools:${env.PATH}"
    }

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

        stage('Android Ortam Kontrolü') {
            when {
                expression { params.PLATFORM == 'Android' }
            }
            steps {
                script {
                    try {
                        sh '''
                            echo "🔍 Android SDK kontrolü:"
                            echo "ANDROID_HOME: $ANDROID_HOME"

                            echo "📱 Bağlı cihazlar kontrol ediliyor..."
                            adb version
                            adb devices

                            if ! adb devices | grep -q "device$"; then
                                echo "❌ Hiç cihaz bulunamadı veya yetkisiz!"
                                echo "📋 Kontrol edilecekler:"
                                echo "1. Fiziksel cihaz bağlı mı?"
                                echo "2. Cihazda USB hata ayıklama açık mı?"
                                echo "3. Cihaz yetkili mi? (Cihaz ekranını kontrol edin)"
                                exit 1
                            fi

                            echo "✅ Cihaz bağlantısı başarılı"
                        '''
                    } catch (Exception e) {
                        echo """
                        ❌ Android Ortam Hatası
                        Hata: ${e.message}

                        🔍 Kontrol Listesi:
                        1. Android SDK kurulu mu? ($ANDROID_HOME)
                        2. Platform Tools kurulu mu?
                        3. adb çalışıyor mu?
                        4. Jenkins kullanıcısının yetkileri var mı?
                        """
                        currentBuild.result = 'FAILURE'
                        error "Android ortam kontrolü başarısız: ${e.message}"
                    }
                }
            }
        }

        stage('Setup Environment') {
            when {
                expression { params.PLATFORM != 'Web' }
            }
            steps {
                script {
                    try {
                        sh '''
                            echo "Mevcut kurulumları temizleme..."
                            npm uninstall -g appium || true
                            npm uninstall -g appium-doctor || true
                            rm -rf ~/.appium || true
                            rm -rf ~/.npm/_cacache || true
                            npm cache clean -f

                            echo "Node ve npm versiyonları:"
                            node -v
                            npm -v

                            echo "Appium kurulumu yapılıyor..."
                            npm install -g appium@2.5.4
                            appium -v
                        '''

                        if (params.PLATFORM == 'Android') {
                            sh '''
                                echo "uiautomator2 sürücüsü kuruluyor..."
                                appium driver uninstall uiautomator2 || true
                                appium driver install uiautomator2@3.9.8
                                echo "Kurulu sürücüler:"
                                appium driver list
                            '''
                        } else if (params.PLATFORM == 'iOS') {
                            sh '''
                                echo "xcuitest sürücüsü kuruluyor..."
                                appium driver uninstall xcuitest || true
                                appium driver install xcuitest
                                echo "Kurulu sürücüler:"
                                appium driver list
                            '''
                        }

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
                            echo "Var olan Appium süreçleri temizleniyor..."
                            pkill -f appium || true
                            sleep 5

                            echo "Appium server başlatılıyor..."
                            appium --allow-insecure chromedriver_autodownload -p 4723 --log-level debug --relaxed-security > appium.log 2>&1 &

                            echo "Server başlaması bekleniyor..."
                            sleep 30

                            echo "Server durumu kontrol ediliyor..."
                            if curl -s http://localhost:4723/status | grep -q "status.*0"; then
                                echo "✅ Appium server başarıyla çalışıyor"
                            else
                                echo "❌ Appium server başlatılamadı"
                                echo "📄 Appium logları:"
                                cat appium.log
                                exit 1
                            fi
                        '''
                    } catch (Exception e) {
                        echo "❌ Appium Server Hatası: ${e.message}"
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
                        sh '''
                            echo "🔧 Rapor dizinleri oluşturuluyor..."
                            mkdir -p target/cucumber-reports
                            mkdir -p target/allure-results

                            echo "🚀 Testler başlatılıyor..."
                            CUCUMBER_PUBLISH_TOKEN='' mvn clean test \
                            -Dplatform="${PLATFORM}" \
                            -Dcucumber.plugin="json:target/cucumber-reports/cucumber.json,pretty" \
                            -Dcucumber.publish.enabled=false \
                            -Dallure.results.directory=target/allure-results \
                            -Dmaven.test.failure.ignore=true

                            echo "📊 Rapor dosyaları kontrol ediliyor..."
                            find target/cucumber-reports -name "*.json" -type f
                            find target/allure-results -type f
                        '''

                        // Cucumber JSON dosyasının varlığını kontrol et
                        if (!fileExists('target/cucumber-reports/cucumber.json')) {
                            error "Cucumber JSON rapor dosyası oluşturulamadı!"
                        }
                    } catch (Exception e) {
                        echo """
                        ❌ Test Hatası
                        Hata Mesajı: ${e.message}

                        🔍 Debug Bilgileri:
                        - Çalışma Dizini: ${pwd()}
                        - Platform: ${params.PLATFORM}
                        - Build No: ${env.BUILD_NUMBER}

                        📋 Kontrol Listesi:
                        1. pom.xml'de cucumber-reporting dependency var mı?
                        2. Test sınıflarında @CucumberOptions doğru yapılandırılmış mı?
                        3. target/cucumber-reports dizini oluşturulabildi mi?
                        """

                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // Appium'u durdur
                sh 'pkill -f appium || true'

                // Test sonuçlarını arşivle
                archiveArtifacts artifacts: 'target/cucumber-reports/**, target/allure-results/**', allowEmptyArchive: true

                // Cucumber raporu oluştur
                cucumber buildStatus: 'UNSTABLE',
                    reportTitle: 'Cucumber Report',
                    fileIncludePattern: '**/cucumber.json',
                    jsonReportDirectory: 'target/cucumber-reports',
                    trendsLimit: 10,
                    classifications: [
                        [
                            'key': 'Platform',
                            'value': params.PLATFORM
                        ],
                        [
                            'key': 'Branch',
                            'value': env.BRANCH_NAME ?: 'unknown'
                        ]
                    ]

                // Allure raporu oluştur
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])

                cleanWs()

                echo """
                📊 Test Sonuçları:
                📱 Platform: ${params.PLATFORM}
                🌿 Branch: ${env.BRANCH_NAME ?: 'unknown'}
                ⚠️ Status: ${currentBuild.result}
                """
            }
        }
    }
}