import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

const errorRate = new Rate('error_rate');
const workspaceGetDuration = new Trend('workspace_get_duration');
const projectCreateDuration = new Trend('project_create_duration');
const projectGetDuration = new Trend('project_get_duration');
const projectDeleteDuration = new Trend('project_delete_duration');

const BASE_URL = 'http://localhost:8888/api/v1';

const TEST_USER = {
  email: 'user@gmail.com',
  password: '123456',
};

export const options = {
  stages: [
    { duration: '30s', target: 10  },
    { duration: '1m',  target: 30  },
    { duration: '1m',  target: 60  },
    { duration: '1m',  target: 100 },
    { duration: '30s', target: 120 },
    { duration: '30s', target: 0   },
  ],
  thresholds: {
    'http_req_duration': ['p(95)<500'],
    'error_rate': ['rate<0.05'],
    'workspace_get_duration': ['p(95)<300'],
    'project_create_duration': ['p(95)<800'],
  },
};

export function setup() {
  const res = http.post(
    `${BASE_URL}/auth/token`,
    JSON.stringify({ email: TEST_USER.email, password: TEST_USER.password }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const success = check(res, {
    'setup: login status 200': (r) => r.status === 200,
    'setup: có token': (r) => r.json('result.token') !== undefined,
  });

  if (!success) {
    console.error('Setup failed — không lấy được token:', res.body);
  }

  return { token: res.json('result.token') };
}

export default function (data) {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  group('Workspace', () => {
    const getWorkspace = http.get(`${BASE_URL}/workspaces/me`, { headers });
    workspaceGetDuration.add(getWorkspace.timings.duration);

    const workspaceOk = check(getWorkspace, {
      'GET workspace: status 200': (r) => r.status === 200,
      'GET workspace: có data': (r) => r.json('result') !== null,
      'GET workspace: < 300ms': (r) => r.timings.duration < 300,
    });
    errorRate.add(!workspaceOk);

    sleep(0.5);

    const getProjects = http.get(
      `${BASE_URL}/workspaces/me/projects?page=1&size=10`,
      { headers }
    );

    check(getProjects, {
      'GET projects in workspace: status 200': (r) => r.status === 200,
      'GET projects in workspace: < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(0.5);

    const updateWorkspace = http.patch(
      `${BASE_URL}/workspaces/me`,
      JSON.stringify({ name: `Workspace ${Date.now()}` }),
      { headers }
    );

    check(updateWorkspace, {
      'PATCH workspace: status 200': (r) => r.status === 200,
      'PATCH workspace: < 500ms': (r) => r.timings.duration < 500,
    });
  });

  sleep(1);

  group('Project CRUD', () => {
    const createRes = http.post(
      `${BASE_URL}/projects`,
      JSON.stringify({
        name: `Test Project ${Date.now()}`,
        description: 'k6 stress test project',
      }),
      { headers }
    );
    projectCreateDuration.add(createRes.timings.duration);

    const createOk = check(createRes, {
      'POST project: status 200 hoặc 201': (r) => r.status === 200 || r.status === 201,
      'POST project: có projectId': (r) => r.json('result.id') !== undefined,
      'POST project: < 800ms': (r) => r.timings.duration < 800,
    });
    errorRate.add(!createOk);

    if (!createOk) {
      sleep(1);
      return;
    }

    const projectId = createRes.json('result.id');
    sleep(0.5);

    const getRes = http.get(`${BASE_URL}/projects/${projectId}`, { headers });
    projectGetDuration.add(getRes.timings.duration);

    check(getRes, {
      'GET project: status 200': (r) => r.status === 200,
      'GET project: đúng id': (r) => r.json('result.id') === projectId,
      'GET project: < 300ms': (r) => r.timings.duration < 300,
    });

    sleep(0.5);

    const updateRes = http.patch(
      `${BASE_URL}/projects/${projectId}`,
      JSON.stringify({ name: `Updated ${Date.now()}` }),
      { headers }
    );

    check(updateRes, {
      'PATCH project: status 200': (r) => r.status === 200,
      'PATCH project: < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(0.5);

    const statsRes = http.get(
      `${BASE_URL}/projects/${projectId}/statistics`,
      { headers }
    );

    check(statsRes, {
      'GET project statistics: status 200': (r) => r.status === 200,
      'GET project statistics: < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(0.5);

    const deleteRes = http.del(`${BASE_URL}/projects/${projectId}`, null, { headers });
    projectDeleteDuration.add(deleteRes.timings.duration);

    check(deleteRes, {
      'DELETE project: status 200': (r) => r.status === 200,
      'DELETE project: < 500ms': (r) => r.timings.duration < 500,
    });
  });

  sleep(1);
}

export function teardown(data) {
  console.log('Stress test hoàn thành.');
  console.log(`Token dùng: ${data.token ? 'OK' : 'MISSING'}`);
}
