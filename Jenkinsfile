pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
               sh 'make'
               archiveArtifacts artifacts: '**/docker/*.jar', fingerprint: true
            }
        }
        stage('Publish') {
            environment {
                registryCredential = 'dockerhub'
            }
            steps{
                script {
                    def appimage = docker.build("eureka-service:${env.BUILD_ID}", "-f docker/Dockerfile .")
                    docker.withRegistry( '', registryCredential ) {
                        appimage.push()
                        appimage.push('latest')
                    }
                }
            }
        }
    }
}