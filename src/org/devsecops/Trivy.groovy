package org.devsecops

class Trivy {
    def steps

    Trivy(steps) {
        this.steps = steps
    }

    void scan(String image) {
        steps.echo "🔍 Scanning image with Trivy: ${image}"

        try {
            steps.sh """
                docker run --rm \
                  -v /var/run/docker.sock:/var/run/docker.sock \
                  aquasec/trivy image --severity HIGH,CRITICAL ${image}
            """
            steps.echo "✅ Trivy scan completed successfully."
        } catch (Exception e) {
            steps.echo "❌ Trivy scan failed: ${e.getMessage()}"
            throw e
        }
    }
}
