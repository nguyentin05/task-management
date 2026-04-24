import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const errorRate = new Rate('error_rate');
const authFailDuration = new Trend('auth_fail_duration');

const BASE_URL = 'http://localhost:8888/api/v1';

const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' })).replace(/=/g, '');
const payload = btoa(JSON.stringify({ sub: 'test', iat: 0 })).replace(/=/g, '');

const invalidTokens = [
  'Bearer invalid-token-format',
  'Bearer ' + [header, payload, 'INVALIDSIGNATURE'].join('.'),
  'Bearer ' + [header, 'INVALIDPAYLOAD', 'INVALIDSIGNATURE'].join('.'),
  'Bearer ',
  'Basic dXNlcjpwYXNz',
  'Bearer expired.token.here',
  'Bearer ' + 'a'.repeat(300),
];

export const options = {
  scenarios: {
    invalid_token_test: {
      executor: 'constant-arrival-rate',
      rate: 100,
      timeUnit: '1s',
      duration: '10s',
      preAllocatedVUs: 50,
      maxVUs: 100,
    },
  },
  thresholds: {
    'http_req_failed': ['rate<0.01'],
    'error_rate': ['rate==0'],
    'auth_fail_duration': ['p(95)<100'],
  },
};

export default function () {
  const token = invalidTokens[Math.floor(Math.random() * invalidTokens.length)];

  const headers = {
    'Authorization': token,
    'Content-Type': 'application/json',
  };

  const res = http.get(`${BASE_URL}/workspaces/me`, { headers });

  authFailDuration.add(res.timings.duration);

  const isUnauthorized = res.status === 401;
  const isFastEnough = res.timings.duration < 100;
  const success = isUnauthorized && isFastEnough;

  errorRate.add(!success);

  if (!success) {
    console.error(`FAIL: status=${res.status}, duration=${res.timings.duration}ms, token=${token.substring(0, 30)}...`);
  }

  check(res, {
    'status is 401': (r) => r.status === 401,
    'response time < 100ms': (r) => r.timings.duration < 100,
  });

  sleep(0.01);
}