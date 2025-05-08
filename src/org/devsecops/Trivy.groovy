package org.devsecops

class Gitleaks {
    def steps

    Gitleaks(steps) {
        this.steps = steps
    }

    void scan() {
        steps.echo "🔐 Running fast GitLeaks scan on key folders..."

        try {
            // Adjust paths to your real subfolders
            def scanTargets = [
                "${steps.env.WORKSPACE}/src",
                "${steps.env.WORKSPACE}/app",
                "${steps.env.WORKSPACE}/config"
            ]

            for (path in scanTargets) {
                steps.sh """
                    if [ -d "${path}" ]; then
                      docker run --rm \
                        -v ${path}:/repo \
                        zricethezav/gitleaks detect \
                        --source=/repo \
                        --no-git \
                        --report-format json \
                        --exit-code 0 \
                        --redact
                    else
                      echo "⚠️ Skipping missing path: ${path}"
                    fi
                """
            }

            steps.echo "✅ GitLeaks scan completed successfully."
        } catch (Exception e) {
            steps.echo "❌ GitLeaks scan failed: ${e.getMessage()}"
            throw e
        }
    }
}
