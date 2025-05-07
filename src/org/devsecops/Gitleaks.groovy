package org.devsecops

class Gitleaks {
  static void scan() {
    echo "🔐 Starting GitLeaks secrets scan..."
    
    sh '''
      docker run --rm \
        -v "$(pwd)":/repo \
        zricethezav/gitleaks detect --source=/repo
    '''

    echo "✅ GitLeaks scan completed."
  }
}
