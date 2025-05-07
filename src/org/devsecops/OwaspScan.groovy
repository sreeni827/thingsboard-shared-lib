package org.devsecops

class OwaspScan {
  static void scan() {
    sh '''
      mkdir -p owasp-output
      docker run --rm \
        -v "$(pwd)":/src \
        -v "$(pwd)/owasp-output":/report \
        owasp/dependency-check \
        --project "ThingsBoard" \
        --scan /src \
        --format "HTML" \
        --out /report
    '''
    echo "📄 OWASP Dependency-Check report generated at owasp-output/index.html"
  }
}
