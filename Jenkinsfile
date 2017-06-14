
pipeline {
    agent any

    stages {
        checkout([$class: 'GitSCM', branches: [[name: '**']], 
        browser: [$class: 'GithubWeb', 
        repoUrl: 'https://github.com/javydreamercsw/validation-manager'], 
        doGenerateSubmoduleConfigurations: false, extensions: [], 
        submoduleCfg: [], 
        userRemoteConfigs: [[credentialsId: 'bd07203f-cbb4-452d-a297-5b984a446f9a', 
        url: 'https://github.com/javydreamercsw/validation-manager.git']]])
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