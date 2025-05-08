package org.devsecops

class OwaspScan {
    def steps

    OwaspScan(steps) {
        this.steps = steps
    }

    void scan() {
        steps.echo "🛡️ OWASP Dependency-Check (targeted + optimized)"

        def updateFlag = "--noupdate"
        def cacheDir = "/tmp/owasp-data"
        def sourceDirs = ["application", "common", "transport"]

        if (!steps.fileExists("${cacheDir}/dc.h2.db")) {
            steps.echo "📥 First-time scan: enabling DB download..."
            updateFlag = ""  // let it download
        }

        for (dir in sourceDirs) {
            def path = "${steps.env.WORKSPACE}/src/${dir}"
            if (steps.fileExists(path)) {
                steps.echo "📁 Scanning: ${path}"
                try {
                    steps.sh """
                        docker run --rm \
                          -v ${path}:/src \
                          -v ${steps.env.WORKSPACE}/owasp-output:/report \
                          -v ${cacheDir}:/dependency-check/data \
                          owasp/dependency-check \
                          --project "ThingsBoard-${dir}" \
                          --scan /src \
                          --format "JSON" \
                          --out /report \
                          --disableAssembly \
                          ${updateFlag}
                    """
                } catch (Exception e) {
                    steps.echo "⚠️ OWASP scan failed for ${dir}: ${e.getMessage()}"
                }
            } else {
                steps.echo "⏭ Skipping missing: ${path}"
            }
        }

        steps.archiveArtifacts artifacts: 'owasp-output/*.json', onlyIfSuccessful: false
        steps.echo "✅ OWASP scan completed for available modules."
    }
}
