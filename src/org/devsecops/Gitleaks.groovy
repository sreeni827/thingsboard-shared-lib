package org.devsecops

class Gitleaks {
    def steps

    Gitleaks(steps) {
        this.steps = steps
    }

    void scan() {
        steps.echo "🔐 Running optimized GitLeaks scan..."

        try {
            steps.sh """
                docker run --rm \
                  -v ${steps.env.WORKSPACE}:/repo \
                  zricethezav/gitleaks detect \
                  --source=/repo \
                  --no-git \
                  --report-format json \
                  --exit-code 0 \
                  --redact
            """
            steps.echo "✅ GitLeaks scan completed."
        } catch (Exception e) {
            steps.echo "❌ GitLeaks scan failed: ${e.getMessage()}"
            throw e
        }
    }
}
