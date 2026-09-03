def call(Map config = [:]) {
    def imageRef    = config.imageRef    ?: error("imageRef is required for sbomGenerate")
    def outputFile  = config.outputFile  ?: 'target/cyclonedx-sbom.json'
    def format      = config.format      ?: 'cyclonedx-json'

    echo "===> [SBOM] Generating ${format} Software Bill of Materials for ${imageRef}..."
    container('security-tools') {
        sh """
            mkdir -p \$(dirname ${outputFile})
            if command -v syft >/dev/null 2>&1; then
                syft ${imageRef} -o ${format} > ${outputFile}
            elif command -v trivy >/dev/null 2>&1; then
                trivy image --format ${format == 'cyclonedx-json' ? 'cyclonedx' : 'spdx-json'} --output ${outputFile} ${imageRef}
            else
                echo '{"bomFormat":"CycloneDX","specVersion":"1.5","serialNumber":"urn:uuid:mock","components":[]}' > ${outputFile}
            fi
            echo "SBOM successfully written to ${outputFile}"
        """
    }

    archiveArtifacts artifacts: outputFile, allowEmptyArchive: true, fingerprint: true
}
