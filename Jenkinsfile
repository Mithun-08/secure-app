pipeline {
    agent any

    stages {
        stage('Load Config') {
            steps {
                script {
                    cfg = load 'config.groovy'
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

        stage('Verify Signature') {
            steps {
                sh """
                    cosign verify \
                      --certificate-identity=${cfg.gcpSignerIdentity} \
                      --certificate-oidc-issuer=${cfg.oidcIssuer} \
                      ${env.IMAGE_REF}
                """
            }
        }

	stage('Deploy to K3s') {
            steps {
                sh """
                    kubectl apply -f deployment.yaml -n ${cfg.k3sNamespace}
                    kubectl set image deployment/${cfg.k3sDeploymentName} \
                      ${cfg.k3sContainerName}=${env.IMAGE_REF} \
              	      -n ${cfg.k3sNamespace}
        	"""
           }
       }    

    }
}
