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
                                
                                if [ "${PLATFORM}" = "Android" ]; then
                                    echo "🤖 Android Driver Kurulumu"
                                    appium driver install uiautomator2
                                elif [ "${PLATFORM}" = "iOS" ]; then
                                    echo "🍎 iOS Driver Kurulumu"
                                    appium driver install xcuitest
                                fi
                                
                                echo "✅ Kurulum Tamamlandı"
                            '''
                        }
                    } catch (Exception e) {
                        echo "❌ Kurulum Hatası: ${e.message}"
                        throw e
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
                            appium --log appium.log --relaxed-security &
                            sleep 10
                            
                            if [ "${PLATFORM}" = "Android" ]; then
                                echo "📱 Android Cihaz Kontrolü"
                                adb devices
                            fi
                        '''
                    } catch (Exception e) {
                        echo "❌ Appium Başlatma Hatası: ${e.message}"
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