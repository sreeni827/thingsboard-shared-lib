package org.devsecops

class Gitleaks {
    def steps

    Gitleaks(steps) {
        this.steps = steps
    }

    void scan() {
        steps.echo "🔐 Running GitLeaks scan..."

        try {
            steps.sh """
                docker run --rm \
                  -v ${steps.env.WORKSPACE}:/repo zricethezav/gitleaks \
                  detect --source=/repo --no-git --report-format sarif
            """
            steps.echo "✅ GitLeaks scan completed."
        } catch (Exception e) {
            steps.echo "❌ GitLeaks scan failed: ${e.getMessage()}"
            throw e
        }
    }
}
