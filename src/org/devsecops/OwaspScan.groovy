package org.devsecops

class OwaspScan {
    def steps

    OwaspScan(steps) {
        this.steps = steps
    }

    void scan() {
        steps.echo "🛡️ Starting optimized OWASP Dependency-Check scan..."

        try {
            steps.sh """
                mkdir -p ${steps.env.WORKSPACE}/owasp-output
                docker run --rm \
                  -v ${steps.env.WORKSPACE}:/src \
                  -v ${steps.env.WORKSPACE}/owasp-output:/report \
                  -v /tmp/owasp-data:/dependency-check/data \
                  owasp/dependency-check \
                  --project "ThingsBoard" \
                  --scan /src \
                  --format "HTML" \
                  --out /report \
                  --disableAssembly \
                  --noupdate
            """
            steps.echo "✅ OWASP scan completed. Report: owasp-output/dependency-check-report.html"
            steps.archiveArtifacts artifacts: 'owasp-output/*.html', onlyIfSuccessful: false
        } catch (Exception e) {
            steps.echo "❌ OWASP scan failed: ${e.getMessage()}"
            throw e
        }
    }
}
