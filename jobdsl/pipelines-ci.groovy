// ==============================================================================
// Job DSL: Pure CI Pipelines (Zero Git-Parameter / Webhook & Multibranch Driven)
// ==============================================================================

def apps = [
    [
        name: 'jhipster-microservice',
        description: 'Java 21 / Spring Boot 3 Microservice CI Pipeline',
        repoUrl: 'https://github.com/nubenetes/jhipster-microservice.git',
        jenkinsfile: 'jenkinsfiles/ci/Jenkinsfile.app-java-maven'
    ],
    [
        name: 'angular-frontend',
        description: 'Angular 18 Enterprise Frontend CI Pipeline',
        repoUrl: 'https://github.com/nubenetes/angular-frontend.git',
        jenkinsfile: 'jenkinsfiles/ci/Jenkinsfile.app-angular'
    ]
]

// Multibranch Pipeline: Automatically discovers Branches, Tags, and PRs from Git
// No manual parameters, no UI dropdowns, fully automated CI
apps.each { app ->
    multibranchPipelineJob("01-CI-Build-Pipelines/${app.name}") {
        description("""
        🚀 <b>Pure GitOps CI Multibranch Pipeline: ${app.name}</b><br/>
        ${app.description}<br/>
        • <b>Source Repository</b>: ${app.repoUrl}<br/>
        • <b>Trigger Model</b>: Webhook & Git Push / PR (Zero UI parameters required)<br/>
        • <b>GitOps Integration</b>: Automatically builds, tests, signs, and commits new image tags to ArgoCD GitOps repository.
        """.stripIndent())

        branchSources {
            git {
                id("${app.name}-git-source")
                remote(app.repoUrl)
                includes('main develop staging release/* PR-*')
            }
        }

        orphanedItemStrategy {
            discardOldItems {
                numToKeep(20)
                daysToKeep(15)
            }
        }

        factory {
            workflowBranchProjectFactory {
                scriptPath(app.jenkinsfile)
            }
        }

        triggers {
            periodicFolderTrigger {
                interval('5m')
            }
        }
    }
}
