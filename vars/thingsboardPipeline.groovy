def call() {
  pipeline {
    agent any

    environment {
      DOCKER_IMAGE = "sreenivasuluramanaboina/thingsboard:kafka-v2"
      AWS_REGION = "us-east-1"
    }

    stages {
      /*
      stage('Build Backend') {
        when { expression { return false } }
        steps {
          echo '⏩ Skipping backend build'
        }
      }

      stage('Build Docker Image') {
        when { expression { return false } }
        steps {
          echo '⏩ Skipping Docker image build'
        }
      }

      stage('Trivy Scan') {
        when { expression { return false } }
        steps {
          script {
            def trivy = new org.devsecops.Trivy(this)
            trivy.scan(DOCKER_IMAGE)
          }
        }
      }

      stage('GitLeaks Scan') {
        when { expression { return false } }
        steps {
          script {
            def gitleaks = new org.devsecops.Gitleaks(this)
            gitleaks.scan()
          }
        }
      }

      stage('OWASP Scan') {
        when { expression { return false } }
        steps {
          script {
            def owasp = new org.devsecops.OwaspScan(this)
            owasp.scan()
          }
        }
      }

      stage('Push Docker Image') {
        when { expression { return false } }
        steps {
          withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh '''
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker push $DOCKER_IMAGE
            '''
          }
        }
      }

      stage('Deploy to EKS with Helm') {
        when { expression { return false } }
        steps {
          script {
            sh '''
              aws eks update-kubeconfig --region us-east-1 --name eks-dev
              helm upgrade --install thingsboard ./helm/thingsboard \
                --namespace thingsboard \
                --create-namespace \
                --set image.repository=sreenivasuluramanaboina/thingsboard \
                --set image.tag=kafka-v2
            '''
          }
        }
      }
      */

      stage('Provision EKS Cluster') {
        steps {
          dir('terraform-eks/envs/qa') {
            script {
              withCredentials([[
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: 'aws-credentials'
              ]]) {
                withEnv([
                  'AWS_ACCESS_KEY_ID=' + env.AWS_ACCESS_KEY_ID,
                  'AWS_SECRET_ACCESS_KEY=' + env.AWS_SECRET_ACCESS_KEY,
                  "AWS_DEFAULT_REGION=${env.AWS_REGION}"
                ]) {
                  sh '''
                    terraform init
                    terraform apply -auto-approve -var-file="terraform.tfvars"
                  '''
                }
              }
            }
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
