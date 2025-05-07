package org.devsecops

class Trivy {
  static void scan(String image) {
    echo "🔍 Starting Trivy scan on image: ${image}"

    try {
      sh """
        docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
          aquasec/trivy image --severity HIGH,CRITICAL ${image}
      """
      echo "✅ Trivy scan completed."
    } catch (Exception e) {
      echo "❌ Trivy scan failed: ${e.getMessage()}"
      throw e  // Optional: comment this out to let pipeline continue
    }
  }
}
