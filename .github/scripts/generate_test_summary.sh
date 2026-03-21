#!/bin/bash

services=(
  "authentication-service"
  "profile-service"
  "task-service"
  "comment-service"
  "api-gateway"
)

grand_total=0
grand_passed=0
grand_failed=0
rows=""

for svc in "${services[@]}"; do
  total=0; failed=0; errors=0; skipped=0

  files=$(find "test-results/test-results-${svc}" -name "TEST-*.xml" 2>/dev/null)
  [ -z "$files" ] && continue

  while IFS= read -r file; do
    t=$(grep -oP 'tests="\K[0-9]+'       "$file" | head -1)
    f=$(grep -oP 'failures="\K[0-9]+'    "$file" | head -1)
    e=$(grep -oP 'errors="\K[0-9]+'      "$file" | head -1)
    s=$(grep -oP 'skipped="\K[0-9]+'     "$file" | head -1)
    total=$((total   + ${t:-0}))
    failed=$((failed + ${f:-0}))
    errors=$((errors + ${e:-0}))
    skipped=$((skipped + ${s:-0}))
  done <<< "$files"

  actual_total=$(( total - skipped ))
  actual_failed=$(( failed + errors ))
  passed=$(( actual_total - actual_failed ))
  pct=$(awk "BEGIN { printf \"%.1f\", ($actual_total > 0) ? ($passed / $actual_total * 100) : 0 }")

  grand_total=$((grand_total   + actual_total))
  grand_passed=$((grand_passed + passed))
  grand_failed=$((grand_failed + actual_failed))

  status=$([[ $actual_failed -eq 0 ]] && echo "✅" || echo "❌")
  rows+="| ${status} ${svc} | ${actual_total} | ${passed} | ${actual_failed} | ${pct}% |\n"
done

grand_pct=$(awk "BEGIN { printf \"%.1f\", ($grand_total > 0) ? ($grand_passed / $grand_total * 100) : 0 }")
grand_status=$([[ $grand_failed -eq 0 ]] && echo "✅" || echo "❌")
grand_row="| **${grand_status} Tổng cộng** | **${grand_total}** | **${grand_passed}** | **${grand_failed}** | **${grand_pct}%** |"

cat >> "$GITHUB_STEP_SUMMARY" << EOF
## Báo Cáo CI

### Thống Kê Test Theo Service

| Service | Tổng | Pass | Fail | Tỉ lệ Pass |
|---------|-----:|-------:|-------:|-------------:|
$(printf "%b" "$rows")${grand_row}

---

### Trạng Thái Các Stage

| Stage | Result |
|-------|--------|
| Format | ${FORMAT_RESULT} |
| Test Matrix | ${TEST_RESULT} |

---

### Service Bị Trigger

| Service | Changed |
|---------|:-------:|
| authentication-service | ${CHANGED_AUTHENTICATION} |
| profile-service        | ${CHANGED_PROFILE} |
| task-service           | ${CHANGED_TASK} |
| comment-service        | ${CHANGED_COMMENT} |
| api-gateway            | ${CHANGED_GATEWAY} |
EOF