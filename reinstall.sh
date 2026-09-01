#!/usr/bin/env bash
# ==============================================================================
# Full Wipe & Reinstallation Script
# ==============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "🔄 Running full reinstallation..."
"${SCRIPT_DIR}/destroy.sh"
echo "Waiting 10 seconds for Kubernetes resources to finalize..."
sleep 10
"${SCRIPT_DIR}/deploy.sh"
