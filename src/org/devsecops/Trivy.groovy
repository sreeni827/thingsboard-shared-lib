package org.devsecops

class Trivy {
  static void scan(image) {
    sh """
      docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image ${image}
    """
  }
}
