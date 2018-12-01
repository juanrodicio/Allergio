pipeline {
  agent any
  stages {
    stage('Initialize') {
      steps {
        sh '''ps aux | grep "[a]ctive=test" | awk \'{print $2}\' | xargs kill || true
                '''
      }
    }
    stage('JUnit Tests') {
      steps {
        sh 'mvn clean verify -Dspring.profiles.active=test'
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
  -Dsonar.login=${loginSonar}'''
      }
    }
    stage('Deploy Test') {
      steps {
        sh '''ps aux | grep "[a]ctive=test" | awk \'{print $2}\' | xargs kill || true
JENKINS_NODE_COOKIE=dontKillMe env SERVER.PORT=8082 nohup java -jar -Dspring.profiles.active=test ./target/${artifactId}-${version}.jar > /var/log/jenkins/allergioapp-test.log 2>&1 &'''
        sh '''until [ "$(curl -w \'%{response_code}\' --no-keepalive -o /dev/null --connect-timeout 1 http://localhost:8082/api/getUser?username=admin)" == "200" ];
do echo --- sleeping for 5 second;
sleep 5;
done'''
      }
    }
    stage('API Rest Tests') {
      steps {
        nodejs(nodeJSInstallationName: 'nodejs_11', configId: 'bb9aa746-bac3-443d-937a-2b027d348acd') {
          sh 'newman run ${pathToNewmanTests} -e ${pathToNewmanEnvironment}'
        }

        sh 'ps aux | grep "[a]ctive=test" | awk \'{print $2}\' | xargs kill || true'
      }
    }
    stage('Deploy') {
      steps {
        sh '''ps aux | grep "[a]ctive=prod" | awk \'{print $2}\' | xargs kill || true
JENKINS_NODE_COOKIE=dontKillMe env SERVER.PORT=8081 nohup java -jar -Dspring.profiles.active=prod ./target/${artifactId}-${version}.jar > /var/log/jenkins/allergioapp.log 2>&1 &'''
        sh '''until [ "$(curl -w \'%{response_code}\' --no-keepalive -o /dev/null --connect-timeout 1 http://localhost:8081/api/getUser?username=admin)" == "200" ];
do echo --- sleeping for 5 second;
sleep 5;
done'''
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
    pathToNewmanTests = './src/test/resources/Allergio_Project.postman_collection'
    loginSonar = 'fc7799d1fd630e927e65f8ba95046a12c081c84a'
    pathToNewmanEnvironment = './src/test/resources/AllergioProject.postman_environment'
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