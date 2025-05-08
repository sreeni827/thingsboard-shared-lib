def call() {
  pipeline {
    agent any

    environment {
      DOCKER_IMAGE = "sreenivasuluramanaboina/thingsboard:kafka-v2"
    }

    stages {
      stage('Build Backend') {
        when { expression { return false } }
        steps {
          echo '⏩ Skipping backend build for now'
          // sh 'mvn clean install -DskipTests -pl application,common,dao,transport -am -Dskip.npm -Dskip.yarn -Dskip.ui -Dskip.frontend'
        }
      }

      stage('Build Docker Image') {
        when { expression { return false } }
        steps {
          echo '⏩ Skipping Docker build for now'
          // sh 'docker build -t $DOCKER_IMAGE -f thingsboard-devops/docker/Dockerfile.tb .'
        }
      }

      stage('Trivy Scan') {
        steps {
          script {
            def trivy = new org.devsecops.Trivy(this)
            trivy.scan(DOCKER_IMAGE)
          }
        }
      }

      stage('GitLeaks Scan') {
        steps {
          script {
            def gitleaks = new org.devsecops.Gitleaks(this)
            gitleaks.scan()
          }
        }
      }

      stage('OWASP Scan') {
        steps {
          script {
            def owasp = new org.devsecops.OwaspScan(this)
            owasp.scan()
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
    stage('Provision EKS Cluster') {
  steps {
    dir('terraform-eks/envs/dev') {
      script {
        sh 'terraform init'
        sh 'terraform plan -var-file="terraform.tfvars"'
        sh 'terraform apply -auto-approve -var-file="terraform.tfvars"'
      }
    }
  }
}
    stage('Deploy to EKS with Helm') {
  steps {
    script {
      sh '''
        # Set up kubeconfig for kubectl
        aws eks update-kubeconfig --region us-east-1 --name thingsboard-dev-cluster

        # Deploy or upgrade ThingsBoard using Helm
        helm upgrade --install thingsboard ./helm/thingsboard \
          --namespace thingsboard \
          --create-namespace \
          --set image.repository=sreenivasuluramanaboina/thingsboard \
          --set image.tag=kafka-v2
      '''
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
