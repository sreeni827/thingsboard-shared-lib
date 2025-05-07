package org.devsecops

class Trivy {
  static void scan(String image) {
    echo "🔍 Scanning image with Trivy: ${image}"

    try {
      sh """
        docker run --rm \
          -v /var/run/docker.sock:/var/run/docker.sock \
          aquasec/trivy image --severity HIGH,CRITICAL ${image}
      """
      echo "✅ Trivy scan passed."
    } catch (Exception e) {
      echo "❌ Trivy scan failed: ${e.getMessage()}"
      throw e  // or comment this line if you want to let the pipeline continue
    }
  }
}

