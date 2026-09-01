#!/usr/bin/env bash
# ==============================================================================
# Clean Decommission Script
# ==============================================================================
set -euo pipefail

echo "======================================================================"
echo "🧹 DECOMMISSIONING PLATFORM: Jenkins Without Git Parameter"
echo "======================================================================"

kubectl delete -f argocd-apps/ --ignore-not-found || true

if command -v helm &>/dev/null; then
    helm uninstall jenkins -n jenkins || true
    helm uninstall argocd -n argocd || true
    helm uninstall grafana -n observability || true
    helm uninstall prometheus -n observability || true
    helm uninstall otel-collector -n observability || true
fi

kubectl delete namespace jenkins argocd observability nubenetes-dev-apps nubenetes-staging-apps nubenetes-prod-apps --ignore-not-found || true

echo "✅ All namespaces and resources cleanly terminated."
