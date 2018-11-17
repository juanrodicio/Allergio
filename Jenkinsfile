pipeline {
  agent any
  stages {
    stage('Initialize') {
      steps {
        sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
      }
    }
    stage('Tests') {
      parallel {
        stage('JUnit Tests') {
          steps {
            sh 'mvn clean verify -e -Dspring.profiles.active=test'
          }
        }
        stage('API Rest Tests') {
          steps {
            sh '''. ~/.nvm/nvm.sh
nvm use 8
newman run ./src/test/resources/${apiRestFile}'''
          }
        }
      }
    }
    stage('Build') {
      steps {
        sh 'mvn package -DskipTests=true'
      }
    }
    stage('SonarQube') {
      steps {
        echo 'SonarQube analysis'
        sh '''mvn sonar:sonar \\
  -Dsonar.projectKey=juanrodicio_Allergio \\
  -Dsonar.organization=juanrodicio-github \\
  -Dsonar.host.url=https://sonarcloud.io \\
  -Dsonar.login={loginSonar}'''
      }
    }
    stage('Deploy') {
      steps {
        sh '''ps aux | grep "[a]llergio" | awk \'{print $2}\' | xargs kill || true
JENKINS_NODE_COOKIE=dontKillMe env SERVER.PORT=8081 nohup java -jar -Dspring.profiles.active=prod ./target/${artifactId}-${version}.jar > /dev/null 2>&1 &'''
      }
    }
  }
  tools {
    maven 'mvn'
    jdk 'jdk8'
  }
  environment {
    groupId = readMavenPom().getGroupId()
    artifactId = readMavenPom().getArtifactId()
    version = readMavenPom().getVersion()
    apiRestFile = 'Allergio_Project.postman_collection.json'
    loginSonar = 'fc7799d1fd630e927e65f8ba95046a12c081c84a'
  }
  post {
    always {
      archiveArtifacts(artifacts: 'target/*.jar', fingerprint: true)
      junit 'target/surefire-reports/*.xml'

    }

  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }
}