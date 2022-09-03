pipeline {
    agent any

    triggers {
        pollSCM '* * * * *'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Roushangopal/jgsu-spring-petclinic.git'
            }
        }
        stage('Build') {
            steps {
                // Run Maven on a Unix agent.
                sh './mvnw clean package'
            }

            post {
                success {
                      emailext attachLog: true, 
                      body: 'Please go to ${BUILD_URL} and verify the build', 
                      to: "test@jenkins.com",
                      compressLog: true, 
                      recipientProviders: [upstreamDevelopers()], 
                      subject: 'Job \'${JOB_NAME}\' (${BUILD_NUMBER}) is waiting for input'
                }
            }
        }
        stage('Post') {
            steps {
                echo "Done"
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
    }
}