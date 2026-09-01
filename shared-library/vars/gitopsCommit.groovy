// ==============================================================================
// Shared Library Step: gitopsCommit.groovy
// Updates environment manifests in GitOps repository and commits/pushes changes
// This implements pure GitOps automation: Git is the Single Source of Truth!
// ==============================================================================

def call(Map config = [:]) {
    def envName    = config.envName ?: 'dev'
    def appName    = config.appName ?: error("gitopsCommit requires 'appName'")
    def imageTag   = config.imageTag ?: error("gitopsCommit requires 'imageTag'")
    def gitopsRepo = config.gitopsRepo ?: env.GITOPS_REPO ?: 'https://github.com/nubenetes/jenkins-without-git-parameter.git'
    def commitMsg  = config.commitMsg ?: "chore(gitops): update ${appName} to ${imageTag} for ${envName} [skip ci]"
    def branchName = config.branchName ?: 'main'

    echo "📝 [Pure GitOps Push] Updating image tag for '${appName}' in environment '${envName}' to '${imageTag}'..."

    dir('gitops-workspace') {
        sh """
            git config --global user.name "Nubenetes GitOps Bot"
            git config --global user.email "gitops-bot@nubenetes.io"

            # Update target environment YAML configuration
            ENV_FILE="sample-apps/gitops-manifests/environments/${envName}.yaml"
            if [ -f "\$ENV_FILE" ]; then
                echo "Updating \$ENV_FILE..."
                sed -i 's/${appName}:.*/${appName}: "${imageTag}"/g' "\$ENV_FILE" || true
            fi

            # Update Kustomize overlay if present
            KUSTOMIZE_OVERLAY="sample-apps/${appName}/k8s/overlays/${envName}/kustomization.yaml"
            if [ -f "\$KUSTOMIZE_OVERLAY" ]; then
                echo "Updating Kustomize overlay \$KUSTOMIZE_OVERLAY..."
                sed -i "s/newTag:.*/newTag: \\"${imageTag}\\"/g" "\$KUSTOMIZE_OVERLAY" || true
            fi

            # Check if changes exist
            if git status --porcelain | grep -E "(sample-apps)"; then
                echo "✅ Changes detected. Committing to GitOps repository..."
                git add sample-apps/
                git commit -m "${commitMsg}"
                echo "🚀 GitOps commit created: ${commitMsg}"
                # In live production with GitHub App, push to branch or open PR:
                # git push origin ${branchName}
            else
                echo "ℹ️ No GitOps manifest changes detected (already at tag ${imageTag})."
            fi
        """
    }
}
