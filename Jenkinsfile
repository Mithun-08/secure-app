pipeline {
    agent any

    stages {
        stage('Load Config') {
            steps {
                script {
                    cfg = [
                        registry            : env.REGISTRY,
                        imageName           : env.IMAGE_NAME,
                        ghcrCredentialsId   : env.GHCR_CREDENTIALS_ID,
                        gcpKeyCredentialsId : env.GCP_KEY_CREDENTIALS_ID,
                        gcpSignerIdentity   : env.GCP_SIGNER_IDENTITY,
                        oidcIssuer          : env.OIDC_ISSUER,
                        k3sNamespace        : env.K3S_NAMESPACE,
                        k3sDeploymentName   : env.K3S_DEPLOYMENT_NAME,
                        k3sContainerName    : env.K3S_CONTAINER_NAME
                    ]
                }
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test package'
            }
        }

        stage('Build Image') {
            steps {
                script {
                    env.IMAGE_REF = "${cfg.registry}/${cfg.imageName}:${BUILD_NUMBER}"
                }
                sh "podman build -t ${env.IMAGE_REF} ."
            }
        }

        stage('Push Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: cfg.ghcrCredentialsId,
                    usernameVariable: 'GHCR_USER',
                    passwordVariable: 'GHCR_PAT'
                )]) {
                    sh """
                        mkdir -p \$HOME/.docker
                        echo \$GHCR_PAT | podman login ${cfg.registry} -u \$GHCR_USER --password-stdin --authfile=\$HOME/.docker/config.json
                        podman push --authfile=\$HOME/.docker/config.json ${env.IMAGE_REF}
                    """
                }
            }
        }

        stage('Sign Image (Keyless)') {
            steps {
                withCredentials([file(credentialsId: cfg.gcpKeyCredentialsId, variable: 'GCP_KEY')]) {
                    sh """
                        gcloud auth activate-service-account --key-file=\$GCP_KEY
                        TOKEN=\$(gcloud auth print-identity-token --audiences=sigstore)
                        cosign sign --yes --identity-token="\$TOKEN" ${env.IMAGE_REF}
                    """
                }
            }
        }
    }
}
