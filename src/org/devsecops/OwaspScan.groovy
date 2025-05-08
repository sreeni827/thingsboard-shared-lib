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

        // check if DB exists
        def dbExists = steps.fileExists("${cacheDir}/dc.h2.db")

        if (!dbExists) {
            steps.echo "📥 OWASP DB not found in cache. Will auto-update."
            updateFlag = ""  // allow download
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
                  --format "HTML" \
                  --out /report \
                  --disableAssembly \
                  ${updateFlag}
            """
            steps.echo "✅ OWASP scan complete. Report saved to: owasp-output/dependency-check-report.html"
            steps.archiveArtifacts artifacts: 'owasp-output/*.html', onlyIfSuccessful: false
        } catch (Exception e) {
            steps.echo "❌ OWASP scan failed: ${e.getMessage()}"
            throw e
        }
    }
}
