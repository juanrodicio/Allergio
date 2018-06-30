pipeline {
  agent any
  stages {
    stage('Inicializacion') {
      steps {
        sh '''echo "PATH = ${PATH}"
echo "M2_HOME = ${M2_HOME}"
                '''
      }
    }
    stage('Pruebas') {
      steps {
        sh 'mvn clean verify -Dspring.profiles.active=test'
      }
    }
    stage('Compilacion y Entrega') {
      steps {
        sh '''mvn package -DskipTests=true
                
                '''
      }
    }
    stage('Despliege') {
      steps {
        sh '''ps | grep "${artifactId}" | awk \'{print $1}\' | xargs kill -9 || true
env SERVER.PORT=8081 nohup java -jar -Dspring.profiles.active=prod ./target/${artifactId}-${version}.jar > /dev/null 2>&1 &
                '''
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
      junit 'target/surefire-reports/*.xml'

    }

  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }
}