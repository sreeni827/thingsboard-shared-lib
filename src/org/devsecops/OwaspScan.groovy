package org.devsecops

class OwaspScan {
  static void scan() {
    echo "🛡️ Starting OWASP Dependency-Check scan..."

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

    echo "✅ OWASP scan complete. Report: owasp-output/dependency-check-report.html"
  }
}
