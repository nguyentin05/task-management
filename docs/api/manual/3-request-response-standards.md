## 3. Request / Response Standards

### 3.1. Request Headers

| Key           | Value              |
|---------------|--------------------|
| Content-Type  | `application/json` |
| Authorization | `Bearer <token>`   |

### 3.2. Response Structure

All responses are wrapped in a unified JSON structure:

```json
{
  "code": "<code>", // Integer — Result code
  "message": "...", // String — Message
  "result": {...}   // Object — Returned data
}
```

### 3.3. Pagination

All list-returning APIs support pagination via query params:

```
?page=<page>&size=<size>
```

Pagination response:

```json
{
  "code": "<code>",
  "message": "...",
  "result": {
    "currentPage": "<currentPage>",     // Current page
    "totalPages": "<totalPages>",       // Total number of pages
    "pageSize": "<pageSize>",           // Elements per page
    "totalElements": "<totalElements>", // Total number of elements
    "data": [...]                       // Data for the current page
  }
}
```

### 3.4. Date / Time Format

*(Under development)*