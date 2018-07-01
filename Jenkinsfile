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
      steps {
        sh 'mvn clean verify'
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
      }
    }
    stage('Deploy') {
      steps {
        sh 'BUILD_ID=dontKillMe env SERVER.PORT=8081 nohup java -jar -Dspring.profiles.active=prod ./target/${artifactId}-${version}.jar > /dev/null 2>&1 &'
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