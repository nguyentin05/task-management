import http from 'k6/http';
import { check } from 'k6';
import { Rate, Counter } from 'k6/metrics';

const errorRate = new Rate('error_rate');
const totalRequests = new Counter('total_requests');
const successRequests = new Counter('success_requests');

const BASE_URL = 'http://localhost:8888/api/v1';

export const options = {
  scenarios: {
    capacity_test: {
      executor: 'ramping-arrival-rate',
      startRate: 10,
      timeUnit: '1s',
      preAllocatedVUs: 50,
      maxVUs: 200,
      stages: [
        { duration: '30s', target: 80 },
        { duration: '2m',  target: 80 },
        { duration: '30s', target: 120 },
        { duration: '1m',  target: 120 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    'error_rate': ['rate==0'],
    'http_req_duration': ['p(95)<2000'],
    'http_reqs': ['rate>50'],
  },
};

export function setup() {
  const loginRes = http.post(
      `${BASE_URL}/auth/token`,
      JSON.stringify({ email: 'user@gmail.com', password: '123456' }),
      { headers: { 'Content-Type': 'application/json' } }
  );

  const token = loginRes.json('result.token');
  if (!token) {
    throw new Error('Login failed');
  }

  return { token };
}

export default function (data) {
  const headers = {
    'Authorization': `Bearer ${data.token}`,
    'Content-Type': 'application/json',
  };

  const endpoints = [
    `${BASE_URL}/workspaces/me`,
    `${BASE_URL}/workspaces/me/projects?page=1&size=10`,
    `${BASE_URL}/projects?page=1&size=10`,
  ];

  const url = endpoints[Math.floor(Math.random() * endpoints.length)];

  const res = http.get(url, { headers });

  totalRequests.add(1);
  const isSuccess = res.status >= 200 && res.status < 300;
  if (isSuccess) successRequests.add(1);
  errorRate.add(!isSuccess);

  check(res, {
    'status is 2xx': (r) => r.status >= 200 && r.status < 300,
  });
}