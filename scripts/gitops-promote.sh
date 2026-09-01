#!/usr/bin/env bash
# ==============================================================================
# GitOps Release Promotion CLI Helper
# Paradigm: Declarative GitOps (Commit/Tag-based Promotion for ArgoCD)
# ==============================================================================
set -euo pipefail

APP_NAME="${1:-jhipster-microservice}"
TARGET_ENV="${2:-staging}"
IMAGE_TAG="${3:-latest}"

echo "======================================================================"
echo "🚀 GITOPS RELEASE PROMOTION: ${APP_NAME} -> ${TARGET_ENV} (${IMAGE_TAG})"
echo "======================================================================"

MANIFEST_FILE="sample-apps/gitops-manifests/environments/${TARGET_ENV}.yaml"
KUSTOMIZE_FILE="sample-apps/${APP_NAME}/k8s/overlays/${TARGET_ENV}/kustomization.yaml"

if [ -f "${MANIFEST_FILE}" ]; then
    echo "Updating ${MANIFEST_FILE}..."
    sed -i "s/${APP_NAME}:.*/${APP_NAME}: \"${IMAGE_TAG}\"/g" "${MANIFEST_FILE}"
fi

if [ -f "${KUSTOMIZE_FILE}" ]; then
    echo "Updating ${KUSTOMIZE_FILE}..."
    sed -i "s/newTag:.*/newTag: \"${IMAGE_TAG}\"/g" "${KUSTOMIZE_FILE}"
fi

echo "✅ Manifests updated."
echo "Creating Git commit for promotion..."
git status --short sample-apps/
echo "To commit and push to Git (triggering ArgoCD sync):"
echo "  git commit -am 'feat(gitops): promote ${APP_NAME} to ${IMAGE_TAG} for ${TARGET_ENV}'"
echo "  git push origin main"
