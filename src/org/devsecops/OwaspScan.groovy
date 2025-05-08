package org.devsecops


class OwaspScan {
    def steps

    OwaspScan(steps) {
        this.steps = steps
    }

    void scan() {
        steps.echo "🛡️ Starting OWASP Dependency-Check scan..."

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
    --out /report
"""

        steps.echo "✅ OWASP scan complete. Report saved to: owasp-output/dependency-check-report.html"
    }
}
