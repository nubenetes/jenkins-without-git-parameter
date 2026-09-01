#!/usr/bin/env bash
# ==============================================================================
# Register OpenShift STAGING and PROD Clusters in ArgoCD 3.5
# ==============================================================================
set -euo pipefail

echo "===> Registering Multi-Cluster secrets in ArgoCD 3.5..."

# Cluster 2: OCP STAGING
cat << 'SECRET' | kubectl apply -f -
apiVersion: v1
kind: Secret
metadata:
  name: cluster-ocp-staging
  namespace: argocd
  labels:
    argocd.argoproj.io/secret-type: cluster
type: Opaque
stringData:
  name: ocp-staging-cluster
  server: https://api.ocp-staging.nubenetes.internal:6443
  config: |
    {
      "tlsClientConfig": {
        "insecure": true
      }
    }
SECRET

# Cluster 3: OCP PROD
cat << 'SECRET' | kubectl apply -f -
apiVersion: v1
kind: Secret
metadata:
  name: cluster-ocp-prod
  namespace: argocd
  labels:
    argocd.argoproj.io/secret-type: cluster
type: Opaque
stringData:
  name: ocp-prod-cluster
  server: https://api.ocp-prod.nubenetes.internal:6443
  config: |
    {
      "tlsClientConfig": {
        "insecure": true
      }
    }
SECRET

echo "===> ArgoCD Multi-Cluster Registration Completed."
