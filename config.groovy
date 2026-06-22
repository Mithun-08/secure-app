return [
    registry            : 'ghcr.io',
    imageName           : 'mithun-08/hello-java',
    ghcrCredentialsId   : 'ghcr-credentials',
    gcpKeyCredentialsId : 'gcp-signer-key',
    gcpSignerIdentity   : 'signer@stackbill-supply-chain-1842.iam.gserviceaccount.com',
    oidcIssuer          : 'https://accounts.google.com',
    k3sNamespace        : 'default',
    k3sDeploymentName   : 'secure-java-payload',
    k3sContainerName    : 'java-app'
]
