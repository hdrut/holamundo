pipeline{
    agent {
         label 'master'
    }
    /*
    tools {
         maven 'maven 3.6'
         jdk 'java'
    }
    */
    environment {
        // This can be nexus3 or nexus2
        NEXUS_VERSION = "nexus3"
        // This can be http or https
        NEXUS_PROTOCOL = "http"
        // Where your Nexus is running. 'nexus-3' is defined in the docker-compose file
        NEXUS_URL = "37.27.7.42:8081"
        // Repository where we will upload the artifact
        NEXUS_REPOSITORY = "maven-releases"
        // Jenkins credential id to authenticate to Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexus_jenkins"
        
        // Workfolder
        //WORKFOLDER = "/usr/jenkins/node_agent/workspace"
    }

    stages{
        stage('Checkout'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'github_ssh', url: 'git@github.com:hdrut/holamundo.git']]])
            }
        }
        stage('Download artifact from nexus'){
            agent {
                label 'docker'
            }
            steps{
                sh '''
                    pwd 
                    curl -v -u admin:Hola1234 -o app.jar http://37.27.7.42:8081/repository/maven-public/org/springframework/jb-hello-world-maven/0.2.1/jb-hello-world-maven-0.2.1.jar
                '''
            }
        }
        stage('Build container'){
            agent {
                label 'docker'
            }
            steps{
                sh '''
                    docker build -t holamundo .
                '''

            }
        } //fin stage build container
        stage('Deploy container'){
            agent {
                label 'docker'
            }
            steps{
                sh '''
                    docker run -it --name holamundo -p 8080:80 holamundo
                '''

            }
        } //fin stage build container
        
        stage("Post") {
            agent {
                label 'docker'
            }
            steps {
                sh '''
                    pwd
                    echo "Clean up workfolder"
                    rm -Rf *
                '''
            }
        } //fin stage post
        
    }
}
