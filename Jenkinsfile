pipeline {
    agent any

    environment {
        REGISTRY   = "127.0.0.1:5000"
        IMAGE      = "stackbill/hello-java"
        TAG        = "latest"
        GPG_SIGNER = "BCC3B63EC5EEA7D4B29AA1071A712FEA7A7010A9"
    }

    stages {

        stage('Code Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build & Test') {
            steps {
                sh 'mvn clean test package'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Podman Build') {
            steps {
                sh "podman build -t ${REGISTRY}/${IMAGE}:${TAG} ."
            }
        }

        stage('GPG Sign & Push') {
            steps {
                sh "podman push --sign-by ${GPG_SIGNER} ${REGISTRY}/${IMAGE}:${TAG}"
            }
        }

        stage('K3s Deploy') {
            steps {
                sh "podman save ${REGISTRY}/${IMAGE}:${TAG} -o /tmp/secure-app.tar"
                sh "sudo k3s ctr images import /tmp/secure-app.tar"
                sh "sudo kubectl delete deployment secure-java-payload --ignore-not-found=true"
                sh "sudo kubectl apply -f deployment.yaml"
                sh "sudo kubectl rollout status deployment/secure-java-payload --timeout=60s"
            }
            post {
                always {
                    sh "rm -f /tmp/secure-app.tar"
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline complete. Pod is live in K3s."
        }
        failure {
            echo "Pipeline failed. Check stage logs above."
        }
    }
}
