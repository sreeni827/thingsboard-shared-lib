def call() {
  pipeline {
    agent any

    environment {
      DOCKER_IMAGE = "sreenivasuluramanaboina/thingsboard:kafka-v2"
    }

    stages {

      stage('Build Backend') {
        steps {
          sh 'mvn clean install -DskipTests -pl application,common,dao,transport -am -Dskip.npm -Dskip.yarn -Dskip.ui -Dskip.frontend'
        }
      }

      stage('Build Docker Image') {
        steps {
          sh 'docker build -t $DOCKER_IMAGE -f thingsboard-devops/docker/Dockerfile.tb .'
        }
      }

      stage('Trivy Scan') {
        steps {
          script {
            echo "🔍 Running Trivy on image: ${DOCKER_IMAGE}"
            org.devsecops.Trivy.scan(DOCKER_IMAGE)
          }
        }
      }

      stage('GitLeaks Scan') {
        steps {
          script {
            org.devsecops.Gitleaks.scan()
          }
        }
      }

      stage('OWASP Scan') {
        steps {
          script {
            org.devsecops.OwaspScan.scan()
          }
        }
      }

      stage('Push Docker Image') {
        steps {
          withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh '''
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker push $DOCKER_IMAGE
            '''
          }
        }
      }
    }

    post {
      failure {
        echo '❌ Pipeline failed. Check logs.'
      }
      success {
        echo '✅ Pipeline succeeded.'
      }
    }
  }
}
