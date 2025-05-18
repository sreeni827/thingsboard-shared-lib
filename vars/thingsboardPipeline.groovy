def call() {
  pipeline {
    agent any

    environment {
      AWS_REGION = "us-east-1"
    }

    stages {
      stage('Clone Terraform EKS Repo') {
        steps {
          dir('terraform-eks') {
            git url: 'https://github.com/sreeni827/terraform-eks.git', branch: 'main'
          }
        }
      }

      stage('Provision EKS Cluster') {
        steps {
          dir('terraform-eks/envs/qa') {
            script {
              withCredentials([[
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: 'aws-credentials'
              ]]) {
                withEnv([
                  "AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}",
                  "AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}",
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
        echo '✅ EKS cluster provisioned successfully.'
      }
    }
  }
}
