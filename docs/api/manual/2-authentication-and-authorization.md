## 2. Authentication And Authorization

### 2.1. Authentication Mechanism

- The system uses JWT following the Bearer Token standard.
- Every request to a protected endpoint must include the token in the header.

| Field               | Value            |
|---------------------|------------------|
| Authentication Type | JWT Bearer Token |
| Signing Algorithm   | HS512            |

### 2.2. Token-Based Authentication In Requests

The system authenticates using the `Authorization` header in the request:

> `Authorization: Bearer <token>`

### 2.3. Token Lifecycle

| Token         | Expiry   | Purpose                                |
|---------------|----------|----------------------------------------|
| Access Token  | 1 hour   | User authentication and authorization  |
| Refresh Token | 10 hours | Obtain a new Access Token when expired |

### 2.4. Authentication Flow

1. Client calls `POST /auth/token` → receives a JWT token
2. Client attaches the JWT to the HTTP header
3. When the JWT expires → call `POST /auth/refresh`
4. Client calls `POST /auth/refresh` → receives a new JWT token

### 2.5. Public Endpoints

| Method | Endpoint             | Description                                  |
|--------|----------------------|----------------------------------------------|
| POST   | /auth/users/register | Register a new account                       |
| POST   | /auth/token          | Log in and receive a JWT                     |
| POST   | /auth/refresh        | Issue a new JWT from an expired access token |
| POST   | /auth/logout         | Log out and blacklist the current token      |

### 2.6. Authorization

**System Level:**

| Role  | Description          |
|-------|----------------------|
| ADMIN | System administrator |
| USER  | Regular user         |

**Service Level (Task service):**

| Service Role | Description     |
|--------------|-----------------|
| MANAGER      | Project manager |
| MEMBER       | Project member  |