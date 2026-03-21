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
    all_files = glob.glob('test-results/**/TEST-*.xml', recursive=True)
    files = [f for f in all_files if service_name in f]

    print(f'[DEBUG] {service_name}: found {len(files)} files')
    for f in files:
        print(f'  -> {f}')

    if not files:
        return None

    total = failed = errors = skipped = 0

    for file in files:
        try:
            root = ET.parse(file).getroot()
            if root.tag == 'testsuite':
                total   += int(root.attrib.get('tests',    0))
                failed  += int(root.attrib.get('failures', 0))
                errors  += int(root.attrib.get('errors',   0))
                skipped += int(root.attrib.get('skipped',  0))
        except Exception as e:
            print(f'  [WARN] Failed to parse {file}: {e}')

    actual_total  = total - skipped
    actual_failed = failed + errors
    passed        = actual_total - actual_failed
    pct           = round((passed / actual_total) * 100, 1) if actual_total > 0 else 0

    print(f'[DEBUG] {service_name}: total={actual_total} passed={passed} failed={actual_failed} pct={pct}%')

    return {'total': actual_total, 'passed': passed, 'failed': actual_failed, 'pct': pct}


def main():
    print('[DEBUG] Full test-results tree:')
    for root_dir, dirs, files in os.walk('test-results'):
        level = root_dir.replace('test-results', '').count(os.sep)
        indent = '  ' * level
        print(f'{indent}{os.path.basename(root_dir)}/')
        for file in files:
            print(f'{indent}  {file}')

    rows         = []
    grand_total  = 0
    grand_passed = 0
    grand_failed = 0

    for svc in services:
        stats = parse_service(svc)
        if stats is None:
            continue
        grand_total  += stats['total']
        grand_passed += stats['passed']
        grand_failed += stats['failed']
        status = 'PASS' if stats['failed'] == 0 else 'FAIL'
        rows.append(
            f"| {status} | {svc} | {stats['total']} | {stats['passed']} | {stats['failed']} | {stats['pct']}% |"
        )

    grand_pct    = round((grand_passed / grand_total) * 100, 1) if grand_total > 0 else 0
    grand_status = 'PASS' if grand_failed == 0 else 'FAIL'
    grand_row    = (
        f"| **{grand_status}** | **Tong cong** "
        f"| **{grand_total}** "
        f"| **{grand_passed}** "
        f"| **{grand_failed}** "
        f"| **{grand_pct}%** |"
    )

    table_rows = '\n'.join(rows) + ('\n' + grand_row if rows else grand_row)

    summary = f"""## Bao Cao CI

### Thong Ke Test Theo Service

| Status | Service | Tong | Pass | Fail | Ti le Pass |
|--------|---------|-----:|-----:|-----:|-----------:|
{table_rows}

---

### Trang Thai Cac Stage

| Stage | Result |
|-------|--------|
| Format | {os.environ.get('FORMAT_RESULT', 'skipped')} |
| Test Matrix | {os.environ.get('TEST_RESULT', 'skipped')} |

---

### Service Co Trigger

| Service | Changed |
|---------|:-------:|
| authentication-service | {os.environ.get('CHANGED_AUTHENTICATION', 'false')} |
| profile-service        | {os.environ.get('CHANGED_PROFILE',        'false')} |
| task-service           | {os.environ.get('CHANGED_TASK',           'false')} |
| comment-service        | {os.environ.get('CHANGED_COMMENT',        'false')} |
| api-gateway            | {os.environ.get('CHANGED_GATEWAY',        'false')} |
"""

    github_step_summary = os.environ.get('GITHUB_STEP_SUMMARY')
    if github_step_summary:
        with open(github_step_summary, 'a', encoding='utf-8') as f:
            f.write(summary)
    else:
        print(summary)


if __name__ == '__main__':
    main()