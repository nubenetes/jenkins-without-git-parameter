// ==============================================================================
// Shared Library Step: argoAppSync.groovy
// Interacts with ArgoCD 3.5 to trigger synchronization and monitor health gates
// ==============================================================================

def call(Map config = [:]) {
    def appName = config.appName ?: error("argoAppSync step requires 'appName'")
    def targetRevision = config.targetRevision ?: 'HEAD'
    def server = config.server ?: env.ARGOCD_SERVER ?: 'argocd-server.argocd.svc.cluster.local:443'
    def timeoutSeconds = config.timeout ?: 300
    def prune = config.prune != null ? config.prune : true
    def dryRun = config.dryRun ?: false

    echo "----------------------------------------------------------------------"
    echo "⚡ [ArgoCD 3.5] Synchronizing Application: ${appName}"
    echo "⚡ Server:   ${server}"
    echo "⚡ Revision: ${targetRevision}"
    echo "⚡ Timeout:  ${timeoutSeconds}s"
    echo "----------------------------------------------------------------------"

    if (dryRun) {
        echo "⚡ [DRY RUN] Would execute: argocd app sync ${appName} --revision ${targetRevision}"
        return
    }

    // In OpenShift/Kubernetes, use in-cluster token or argocd-token secret
    withCredentials([string(credentialsId: 'argocd-auth-token', variable: 'ARGOCD_AUTH_TOKEN')]) {
        sh """
            export ARGOCD_SERVER="${server}"
            export ARGOCD_AUTH_TOKEN="${ARGOCD_AUTH_TOKEN}"
            export ARGOCD_OPTS="--insecure --grpc-web"

            echo "===> Triggering ArgoCD sync for application '${appName}'..."
            argocd app sync "${appName}" \
                --revision "${targetRevision}" \
                ${prune ? '--prune' : ''} \
                --async || true

            echo "===> Waiting for Application '${appName}' to reach Synced & Healthy status..."
            argocd app wait "${appName}" \
                --health \
                --sync \
                --timeout ${timeoutSeconds}

            echo "===> Application '${appName}' is SYNCED and HEALTHY!"
            argocd app get "${appName}" -o wide
        """
    }
}
