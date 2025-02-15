pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/AdeshM12/jenkins/'
        BRANCH = 'master'
        MAVEN_HOME = '"C:\\Program Files\\apache-maven-3.9.9-bin\\apache-maven-3.9.9\\bin\\mvn.cmd"'
        DOCKER_IMAGE = 'adeshtrivedi/my-maven-project:latest'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        TOMCAT_URL = 'http://localhost:9494/manager/text'
        TOMCAT_CREDENTIALS_ID = 'tomcat-credentials'
        PATH = "C:\\WINDOWS\\system32;C:\\Program Files\\Docker\\Docker\\resources\\bin;C:\\ProgramData\\DockerDesktop\\bin;%PATH%"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Build with Maven') {
            steps {
                bat "${MAVEN_HOME} clean install -f my-maven-project/pom.xml"
            }
        }

        stage('Generate Dockerfile') {
            steps {
                bat '''
                echo FROM openjdk:17-jdk-slim > my-maven-project/Dockerfile
                echo WORKDIR /app >> my-maven-project/Dockerfile
                echo COPY target/my-maven-project-1.0-SNAPSHOT.war /app/my-maven-project.war >> my-maven-project/Dockerfile
                echo EXPOSE 8080 >> my-maven-project/Dockerfile
                echo CMD ["java", "-jar", "/app/my-maven-project.war"] >> my-maven-project/Dockerfile
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    bat "docker build -t ${DOCKER_IMAGE} my-maven-project"
                }
            }
        }

        stage('Login to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        bat """
                        docker login -u %DOCKER_USER% -p %DOCKER_PASS%
                        """
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                bat "docker push ${DOCKER_IMAGE}"
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "${TOMCAT_CREDENTIALS_ID}", usernameVariable: 'TOMCAT_USER', passwordVariable: 'TOMCAT_PASS')]) {
                        bat """
                        curl -u %TOMCAT_USER%:%TOMCAT_PASS% -T my-maven-project/target/my-maven-project-1.0-SNAPSHOT.war "${TOMCAT_URL}/deploy?path=/my-maven-project"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Deployment Successful!"
        }
        failure {
            echo "Deployment Failed!"
        }
    }
}
