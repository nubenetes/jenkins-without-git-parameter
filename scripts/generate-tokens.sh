#!/usr/bin/env bash
# ==============================================================================
# Generate Secret Tokens for Jenkins <-> ArgoCD Integration
# ==============================================================================
set -euo pipefail

echo "===> Generating Jenkins and ArgoCD credentials in namespace 'jenkins'..."

kubectl create secret generic argocd-auth-token \
  --from-literal=ARGOCD_AUTH_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.nubenetes-mock-token-2026" \
  --namespace=jenkins \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic jenkins-admin-credentials \
  --from-literal=JENKINS_ADMIN_PASSWORD="admin123!" \
  --from-literal=JENKINS_SERVICE_PASSWORD="service123!" \
  --namespace=jenkins \
  --dry-run=client -o yaml | kubectl apply -f -

# ArgoCD GitHub Token for ApplicationSet PR Preview Generator
echo "===> Generating GitHub App/PR preview token secret in namespace 'argocd'..."
kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
kubectl create secret generic github-token-secret \
  --from-literal=token="ghp_mock_token_for_pr_preview_2026" \
  --namespace=argocd \
  --dry-run=client -o yaml | kubectl apply -f -

echo "===> Credentials generated successfully."
