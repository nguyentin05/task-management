## 1. Overview

### 1.1. Purpose And Scope

- **Purpose:** Provide technical specification documentation and API integration guidelines for the system.
- **Scope:** This document covers APIs for user authentication and authorization, profile management, and task
  management.

### 1.2. General Information

| Field                 | Value                  |
|-----------------------|------------------------|
| Project Name          | Task Management        |
| Version               | v1.0.0                 |
| Base URL - Local      | http​://localhost:8888 |
| Base URL - Staging    | (under development)    |
| Base URL - Production | (under development)    |
| Protocol              | HTTP / HTTPS           |
| Data Format           | JSON                   |
| Encoding              | UTF-8                  |
| Timezone              | UTC+7                  |

### 1.3. Versioning Strategy

The version is embedded directly in the URL path:

> http​://localhost:8888/api/v1
>
> http​://localhost:8888/api/v2