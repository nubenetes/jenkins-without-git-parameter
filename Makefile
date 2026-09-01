.PHONY: deploy destroy reinstall test lint help

help:
	@echo "Enterprise GitOps Platform Commands:"
	@echo "  make deploy     - Deploy Jenkins (Pure CI), ArgoCD 3.5, and Observability stack"
	@echo "  make destroy    - Clean teardown of all namespaces and resources"
	@echo "  make reinstall  - Full wipe and fresh redeployment"
	@echo "  make promote    - Run GitOps release promotion helper"

deploy:
	@./deploy.sh

destroy:
	@./destroy.sh

reinstall:
	@./reinstall.sh

promote:
	@./scripts/gitops-promote.sh

test:
	@echo "Running lint & schema validations..."
	@which yamllint > /dev/null && yamllint -d relaxed config/ argocd-apps/ helm/ jcasc/ || echo "yamllint not installed, skipping."
