#!/usr/bin/env bash
# ==============================================================================
# OpenShift 4.20+ Security Context Constraints (SCC) & RBAC Setup
# ==============================================================================
set -euo pipefail

echo "===> [OpenShift 4.20+] Configuring Namespaces, ServiceAccounts and RBAC..."

NAMESPACES=("jenkins" "argocd" "observability" "nubenetes-dev-apps" "nubenetes-staging-apps" "nubenetes-prod-apps")

for ns in "${NAMESPACES[@]}"; do
    if command -v oc &>/dev/null; then
        oc get namespace "$ns" &>/dev/null || oc create namespace "$ns"
    elif command -v kubectl &>/dev/null; then
        kubectl get namespace "$ns" &>/dev/null || kubectl create namespace "$ns"
    fi
done

if command -v oc &>/dev/null; then
    echo "===> Binding SCC nonroot / restricted-v2 to Jenkins ServiceAccount..."
    oc adm policy add-scc-to-user nonroot -z jenkins -n jenkins || true
    oc adm policy add-scc-to-user nonroot -z default -n jenkins || true

    echo "===> Granting ClusterRole edit/admin permissions for ephemeral agent management..."
    oc policy add-role-to-user edit -z jenkins -n jenkins || true
    oc policy add-role-to-user edit -z jenkins -n nubenetes-dev-apps || true
fi

echo "===> OpenShift Security Setup Completed."
