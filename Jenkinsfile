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
    stage('Compilacion y Entrega') {
      steps {
        sh '''mvn package -DskipTests=true
                
                '''
      }
    }
    stage('Despliege') {
      steps {
        sh '''echo "Desplegando en el Server"
jps -v | grep "${artifactId}" | awk \'{print $1}\' | xargs kill || true BUILD_ID=dontKillMe env SERVER.PORT=8081 nohup java -jar -Dspring.profiles.active=prod ./target/${artifactId}-${version}.jar > /dev/null 2>&1 &
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