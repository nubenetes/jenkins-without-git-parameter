package com.nubenetes.gitops

class ClusterEnvironment implements Serializable {
    String name
    String environment
    String apiServer
    String ingressDomain
    String registryHost
    String namespace

    ClusterEnvironment(String name, String environment, String apiServer, String ingressDomain, String registryHost, String namespace) {
        this.name = name
        this.environment = environment
        this.apiServer = apiServer
        this.ingressDomain = ingressDomain
        this.registryHost = registryHost
        this.namespace = namespace
    }

    static ClusterEnvironment getForEnvironment(String env) {
        switch(env.toLowerCase()) {
            case 'dev':
                return new ClusterEnvironment('ocp-dev', 'dev', 'https://api.ocp-dev.nubenetes.internal:6443', 'apps.ocp-dev.nubenetes.internal', 'image-registry.openshift-image-registry.svc:5000', 'nubenetes-dev-apps')
            case 'staging':
                return new ClusterEnvironment('ocp-staging', 'staging', 'https://api.ocp-staging.nubenetes.internal:6443', 'apps.ocp-staging.nubenetes.internal', 'image-registry.openshift-image-registry.svc:5000', 'nubenetes-staging-apps')
            case 'prod':
                return new ClusterEnvironment('ocp-prod', 'prod', 'https://api.ocp-prod.nubenetes.internal:6443', 'apps.ocp-prod.nubenetes.internal', 'image-registry.openshift-image-registry.svc:5000', 'nubenetes-prod-apps')
            default:
                throw new IllegalArgumentException("Unknown environment: ${env}")
        }
    }
}
