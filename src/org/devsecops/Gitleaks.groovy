package org.devsecops

class Gitleaks {
  static void scan() {
    sh 'docker run --rm -v $(pwd):/repo zricethezav/gitleaks detect --source=/repo'
  }
}
