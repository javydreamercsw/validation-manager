
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                mvn install
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                echo 'Done!'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                mvn test
                junit '**/target/*.xml'
                step([$class: 'CoberturaPublisher', autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: '**/coverage.xml', failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false])
                echo 'Done!'
            }
        }
    }
}