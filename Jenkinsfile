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
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    sh '''
                        echo "🔧 Ortam Bilgileri:"
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
                                echo "📱 Appium Kurulumu"
                                npm uninstall -g appium || true
                                npm install -g appium@2.5.4
                                
                                echo "🔍 Driver Kontrolü"
                                INSTALLED_DRIVERS=$(appium driver list --installed || true)
                                echo "Kurulu driverlar:"
                                echo "$INSTALLED_DRIVERS"
                                
                                if [ "${PLATFORM}" = "Android" ]; then
                                    echo "🤖 Android Driver Yönetimi"
                                    if echo "$INSTALLED_DRIVERS" | grep -q "uiautomator2"; then
                                        echo "uiautomator2 driver güncelleniyor..."
                                        appium driver update uiautomator2 || true
                                    else
                                        echo "uiautomator2 driver kuruluyor..."
                                        appium driver install uiautomator2 || true
                                    fi
                                elif [ "${PLATFORM}" = "iOS" ]; then
                                    echo "🍎 iOS Driver Yönetimi"
                                    if echo "$INSTALLED_DRIVERS" | grep -q "xcuitest"; then
                                        echo "xcuitest driver güncelleniyor..."
                                        appium driver update xcuitest || true
                                    else
                                        echo "xcuitest driver kuruluyor..."
                                        appium driver install xcuitest || true
                                    fi
                                fi
                                
                                echo "✅ Kurulum Tamamlandı"
                                echo "Son durum:"
                                appium driver list --installed
                            '''
                        }
                    } catch (Exception e) {
                        echo "❌ Kurulum Hatası: ${e.message}"
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
                            echo "🚀 Appium Başlatılıyor..."
                            pkill -f appium || true
                            sleep 2
                            
                            echo "Appium server başlatılıyor..."
                            appium --log appium.log --relaxed-security > /dev/null 2>&1 &
                            
                            echo "Server başlaması bekleniyor..."
                            sleep 10
                            
                            echo "Server durumu kontrol ediliyor..."
                            if curl -s http://localhost:4723/status | grep -q "ready"; then
                                echo "✅ Appium server başarıyla çalışıyor"
                            else
                                echo "❌ Appium server başlatılamadı"
                                cat appium.log
                                exit 1
                            fi
                            
                            if [ "${PLATFORM}" = "Android" ]; then
                                echo "📱 Android Cihaz Kontrolü"
                                adb devices
                                
                                if ! adb devices | grep -q "device$"; then
                                    echo "❌ Bağlı cihaz bulunamadı!"
                                    exit 1
                                fi
                                echo "✅ Android cihaz bağlantısı başarılı"
                            fi
                        '''
                    } catch (Exception e) {
                        echo "❌ Appium Başlatma Hatası: ${e.message}"
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
                            echo "📂 Test Dizinleri Oluşturuluyor..."
                            mkdir -p target/cucumber-reports
                            mkdir -p target/allure-results

                            echo "🧪 Testler Başlatılıyor..."
                            mvn clean test -DplatformName=${params.PLATFORM} -Dcucumber.filter.tags="@${platformTag}"
                        """
                    } catch (Exception e) {
                        echo """
                            ❌ Test Hatası
                            Hata: ${e.message}
                            Platform: ${params.PLATFORM}
                            Build: ${BUILD_NUMBER}
                        """
                        throw e
                    }
                }
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
                    reportTitle: 'Test Sonuçları',
                    buildStatus: 'UNSTABLE'
                )
                
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])

                echo """
                    📊 Test Sonuçları:
                    📱 Platform: ${params.PLATFORM}
                    🌿 Branch: ${env.BRANCH_NAME ?: 'unknown'}
                    🏗️ Status: ${currentBuild.currentResult}
                """
            }
        }
        cleanup {
            cleanWs()
        }
    }
}