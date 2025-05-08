package org.devsecops

class OwaspScan {
    def steps

    OwaspScan(steps) {
        this.steps = steps
    }

    void scan() {
        steps.echo "🛡️ OWASP Dependency-Check (final optimized scan)"

        def updateFlag = "--noupdate"
        def cacheDir = "/tmp/owasp-data"
        def scanPath = steps.env.WORKSPACE

        if (!steps.fileExists("${cacheDir}/dc.h2.db")) {
            steps.echo "📥 OWASP DB not found — will update"
            updateFlag = ""  // allow DB download
        }

        try {
            steps.sh """
                mkdir -p ${scanPath}/owasp-output
                docker run --rm \
                  -v ${scanPath}:/src \
                  -v ${scanPath}/owasp-output:/report \
                  -v ${cacheDir}:/dependency-check/data \
                  owasp/dependency-check \
                  --project "ThingsBoard Full Scan" \
                  --scan /src \
                  --format "JSON" \
                  --out /report \
                  --disableAssembly \
                  ${updateFlag}
            """
            steps.echo "✅ OWASP scan complete."
        } catch (Exception e) {
            steps.echo "⚠️ OWASP scan failed: ${e.getMessage()}"
        }

        if (steps.fileExists("${scanPath}/owasp-output/dependency-check-report.json")) {
            steps.archiveArtifacts artifacts: 'owasp-output/*.json', onlyIfSuccessful: false
        } else {
            steps.echo "📂 No OWASP report found to archive."
        }
    }
}
