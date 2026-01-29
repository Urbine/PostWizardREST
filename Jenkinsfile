pipeline {
  agent { label 'built-in' }  

  tools {
    maven "Maven-Jenkins"
  }

  environment {
    DEPLOY_USER = "jenkins"
    DEPLOY_DIR  = "/opt/liberty/wlp/usr/servers/defaultServer/apps"
    ARTIFACT    = "target/PostWizardREST.war"
    WAR_FILE    = "PostWizardREST.war"       
    SSH_CRED    = "jenkins"
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', 
            changelog: false, 
            credentialsId: '66e84ea5-1cb6-4a43-b1d2-cd2f7ecdb6ae', 
            url: 'git@github.com:Urbine/PostWizardREST.git'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
        stash includes: "${ARTIFACT}", 
        name: "war_file"
        }
    }

    stage('Deploy to agent') {
      agent { label "WMen DevOps" }
      steps {
        unstash "war_file"
        sh  "rm ${DEPLOY_DIR}/${WAR_FILE}"
        sh  "mv ${ARTIFACT} ${DEPLOY_DIR}"
        sh  "rm -rf ${DEPLOY_DIR}/expanded"
        sh  "sudo systemctl restart liberty"
      }
    }
  }

  post {
    always {
      cleanWs()
    }
  }
}
