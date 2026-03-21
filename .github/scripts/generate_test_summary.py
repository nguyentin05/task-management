import xml.etree.ElementTree as ET
import glob
import os

services = [
    'authentication-service',
    'profile-service',
    'task-service',
    'comment-service',
    'api-gateway',
]


def parse_service(service_name):
    total = failed = errors = skipped = 0
    pattern = f'test-results/test-results-{service_name}/TEST-*.xml'
    files = glob.glob(pattern)
    if not files:
        return None
    for file in files:
        try:
            root = ET.parse(file).getroot()
            if root.tag == 'testsuite':
                total   += int(root.attrib.get('tests',    0))
                failed  += int(root.attrib.get('failures', 0))
                errors  += int(root.attrib.get('errors',   0))
                skipped += int(root.attrib.get('skipped',  0))
        except Exception:
            pass
    actual_total  = total - skipped
    actual_failed = failed + errors
    passed        = actual_total - actual_failed
    pct           = round((passed / actual_total) * 100, 1) if actual_total > 0 else 0
    return {
        'total':  actual_total,
        'passed': passed,
        'failed': actual_failed,
        'pct':    pct,
    }


def build_table(
    format_result,
    test_result,
    changed_authentication,
    changed_profile,
    changed_task,
    changed_comment,
    changed_gateway,
):
    rows = []
    grand_total = grand_passed = grand_failed = 0

    for svc in services:
        stats = parse_service(svc)
        if stats is None:
            continue
        grand_total  += stats['total']
        grand_passed += stats['passed']
        grand_failed += stats['failed']
        status = '✅' if stats['failed'] == 0 else '❌'
        rows.append(
            f"| {status} {svc} | {stats['total']} | {stats['passed']} | {stats['failed']} | {stats['pct']}% |"
        )

    grand_pct    = round((grand_passed / grand_total) * 100, 1) if grand_total > 0 else 0
    grand_row    = (
        f"| **{grand_status} Tổng cộng** "
        f"| **{grand_total}** "
        f"| **{grand_passed}** "
        f"| **{grand_failed}** "
        f"| **{grand_pct}%** |"
    )

    table_rows = '\n'.join(rows) + ('\n' + grand_row if rows else grand_row)

    summary = f"""## Báo Cáo CI

### Thống Kê Test Theo Service

| Service | Tổng | Pass | Fail | Tỉ lệ Pass |
|---------|-----:|-------:|-------:|-------------:|
{table_rows}

---

### Trạng Thái Các Stage

| Stage | Result |
|-------|--------|
| Format | {format_result} |
| Test Matrix | {test_result} |

---

### Service Có Trigger

| Service | Changed |
|---------|:-------:|
| authentication-service | {changed_authentication} |
| profile-service        | {changed_profile} |
| task-service           | {changed_task} |
| comment-service        | {changed_comment} |
| api-gateway            | {changed_gateway} |
"""
    return summary


if __name__ == '__main__':
    summary = build_table(
        format_result=os.environ.get('FORMAT_RESULT',  'skipped'),
        test_result=os.environ.get('TEST_RESULT',    'skipped'),
        changed_authentication=os.environ.get('CHANGED_AUTHENTICATION', 'false'),
        changed_profile=os.environ.get('CHANGED_PROFILE',        'false'),
        changed_task=os.environ.get('CHANGED_TASK',           'false'),
        changed_comment=os.environ.get('CHANGED_COMMENT',        'false'),
        changed_gateway=os.environ.get('CHANGED_GATEWAY',        'false'),
    )

    with open(os.environ['GITHUB_STEP_SUMMARY'], 'a', encoding='utf-8') as f:
        f.write(summary)