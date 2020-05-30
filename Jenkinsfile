pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Publish') {
            environment {
                registryCredential = 'dockerhub'
            }
            steps{
                script {
                    def appimage = docker.build("my-image:${env.BUILD_ID}")
                    docker.withRegistry( '', registryCredential ) {
                        appimage.push()
                        appimage.push('latest')
                    }
                }
            }
        }
    }
}