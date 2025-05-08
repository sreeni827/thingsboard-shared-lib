package org.devsecops

class OwaspScan {
    def steps

    OwaspScan(steps) {
        this.steps = steps
    }

    void scan() {
        steps.echo "🛡️ Starting optimized OWASP Dependency-Check scan..."

        def updateFlag = "--noupdate"
        def cacheDir = "/tmp/owasp-data"

        if (!steps.fileExists("${cacheDir}/dc.h2.db")) {
            steps.echo "📥 OWASP DB not found in cache. Will auto-update."
            updateFlag = ""  // first time only
        }

        try {
            steps.sh """
                mkdir -p ${steps.env.WORKSPACE}/owasp-output
                docker run --rm \
                  -v ${steps.env.WORKSPACE}:/src \
                  -v ${steps.env.WORKSPACE}/owasp-output:/report \
                  -v ${cacheDir}:/dependency-check/data \
                  owasp/dependency-check \
                  --project "ThingsBoard" \
                  --scan /src \
                  --format "JSON" \
                  --out /report \
                  --disableAssembly \
                  ${updateFlag}
            """
            steps.echo "✅ OWASP scan complete. Report: owasp-output/dependency-check-report.json"
            steps.archiveArtifacts artifacts: 'owasp-output/*.json', onlyIfSuccessful: false
        } catch (Exception e) {
            steps.echo "❌ OWASP scan failed: ${e.getMessage()}"
            throw e
        }
    }
}
