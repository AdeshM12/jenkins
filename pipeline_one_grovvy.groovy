pipeline {
    agent any

    environment {
        PATH = "${env.PATH};C:\\WINDOWS\\system32;C:\\Users\\HP\\AppData\\Local\\Programs\\Python\\Python310"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'master', url: 'https://github.com/AdeshM12/jenkins/'
            }
        }

        stage('Check Python Version') {
            steps {
                bat 'python --version'
            }
        }

        stage('Run Hello World') {
            steps {
                script {
                    def exitCode = bat(returnStatus: true, script: 'python hello_world.py')
                    if (exitCode != 0) {
                        error("❌ hello_world.py failed!")
                    }
                }
            }
        }

        stage('Run Hello Wipro') {
            steps {
                script {
                    def exitCode = bat(returnStatus: true, script: 'python hello_wipro.py')
                    if (exitCode != 0) {
                        error("❌ hello_wipro.py failed!")
                    }
                }
            }
        }

        stage('Run Hello Jenkins') {
            steps {
                script {
                    def exitCode = bat(returnStatus: true, script: 'python hello_jenkins.py')
                    if (exitCode != 0) {
                        error("❌ hello_jenkins.py failed!")
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ All Python scripts ran successfully!"
        }
        failure {
            echo "❌ One or more Python scripts failed."
        }
    }
}
