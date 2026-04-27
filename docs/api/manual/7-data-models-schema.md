## 7. Data Models / Schema

### 7.1. Authentication Service

**users:**

| Column     | Data Type    | Not NULL | Primary Key | Unique | Default           |
|------------|--------------|----------|-------------|--------|-------------------|
| id         | VARCHAR(255) | ✓        | ✓           | —      | —                 |
| email      | VARCHAR(255) | ✓        | —           | ✓      | —                 |
| password   | VARCHAR(255) | ✓        | —           | —      | —                 |
| created_at | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |

**roles:**

| Column      | Data Type    | Not NULL | Primary Key | Unique | Default |
|-------------|--------------|----------|-------------|--------|---------|
| name        | VARCHAR(255) | ✓        | ✓           | —      | —       |
| description | TEXT         | —        | —           | —      | —       |

**invalidated_tokens:**

| Column      | Data Type    | Not NULL | Primary Key | Unique | Default |
|-------------|--------------|----------|-------------|--------|---------|
| id          | VARCHAR(255) | ✓        | ✓           | —      | —       |
| expiry_time | TIMESTAMPTZ  | ✓        | —           | —      | —       |

**outbox_events:**

| Column      | Data Type    | Not NULL | Primary Key | Unique | Default           |
|-------------|--------------|----------|-------------|--------|-------------------|
| id          | VARCHAR(255) | ✓        | ✓           | —      | —                 |
| routing_key | VARCHAR(255) | ✓        | —           | —      | —                 |
| payload     | TEXT         | ✓        | —           | —      | —                 |
| status      | VARCHAR(50)  | ✓        | —           | —      | —                 |
| created_at  | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |
| retry_count | INTEGER      | —        | —           | —      | 0                 |

**user_role:**

| Column    | Data Type    | Not NULL | Primary Key | Unique | Default |
|-----------|--------------|----------|-------------|--------|---------|
| user_id   | VARCHAR(255) | ✓        | ✓           | —      | —       |
| role_name | VARCHAR(255) | ✓        | ✓           | —      | —       |

 
---

### 7.2. Profile Service

**profiles:**

| Column      | Data Type | Default                                                   |
|-------------|-----------|-----------------------------------------------------------|
| id          | String    | —                                                         |
| userId      | String    | —                                                         |
| avatar      | String    | `https://res.cloudinary.com/.../defaultAvatar_l5nyci.jpg` |
| firstName   | String    | —                                                         |
| lastName    | String    | —                                                         |
| dob         | String    | —                                                         |
| phoneNumber | String    | —                                                         |
| createdAt   | Instant   | CreateDate                                                |

 
---

### 7.3. Task Service

**workspaces:**

| Column      | Data Type    | Not NULL | Primary Key | Unique | Default           |
|-------------|--------------|----------|-------------|--------|-------------------|
| id          | VARCHAR(255) | ✓        | ✓           | —      | —                 |
| user_id     | VARCHAR(255) | ✓        | —           | ✓      | —                 |
| name        | VARCHAR(255) | ✓        | —           | —      | —                 |
| description | TEXT         | —        | —           | —      | —                 |
| created_at  | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |
| updated_at  | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |

**projects:**

| Column      | Data Type    | Not NULL | Primary Key | Unique | Default           |
|-------------|--------------|----------|-------------|--------|-------------------|
| id          | VARCHAR(255) | ✓        | ✓           | —      | —                 |
| name        | VARCHAR(255) | ✓        | —           | —      | —                 |
| description | TEXT         | —        | —           | —      | —                 |
| created_by  | VARCHAR(255) | ✓        | —           | —      | —                 |
| start_at    | TIMESTAMPTZ  | ✓        | —           | —      | —                 |
| end_at      | TIMESTAMPTZ  | ✓        | —           | —      | —                 |
| created_at  | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |
| updated_at  | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |

**columns:**

| Column     | Data Type    | Not NULL | Primary Key | Unique | Default           |
|------------|--------------|----------|-------------|--------|-------------------|
| id         | VARCHAR(255) | ✓        | ✓           | —      | —                 |
| project_id | VARCHAR(255) | ✓        | —           | —      | —                 |
| name       | VARCHAR(255) | ✓        | —           | —      | —                 |
| position   | FLOAT8       | ✓        | —           | —      | —                 |
| created_at | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |
| updated_at | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |

**tasks:**

| Column       | Data Type    | Not NULL | Primary Key | Unique | Default           |
|--------------|--------------|----------|-------------|--------|-------------------|
| id           | VARCHAR(255) | ✓        | ✓           | —      | —                 |
| column_id    | VARCHAR(255) | ✓        | —           | —      | —                 |
| title        | VARCHAR(255) | ✓        | —           | —      | —                 |
| description  | TEXT         | —        | —           | —      | —                 |
| position     | FLOAT8       | ✓        | —           | —      | —                 |
| label        | VARCHAR(255) | —        | —           | —      | —                 |
| start_at     | TIMESTAMPTZ  | ✓        | —           | —      | —                 |
| due_at       | TIMESTAMPTZ  | ✓        | —           | —      | —                 |
| created_by   | VARCHAR(255) | ✓        | —           | —      | —                 |
| created_at   | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |
| updated_at   | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |
| completed_at | TIMESTAMPTZ  | —        | —           | —      | —                 |
| assignee_id  | VARCHAR(255) | —        | —           | —      | —                 |

**workspace_project:**

| Column       | Data Type    | Not NULL | Primary Key | Unique | Default |
|--------------|--------------|----------|-------------|--------|---------|
| workspace_id | VARCHAR(255) | ✓        | ✓           | —      | —       |
| project_id   | VARCHAR(255) | ✓        | ✓           | —      | —       |

**project_member:**

| Column     | Data Type    | Not NULL | Primary Key | Unique | Default |
|------------|--------------|----------|-------------|--------|---------|
| id         | VARCHAR(255) | ✓        | ✓           | —      | —       |
| project_id | VARCHAR(255) | ✓        | —           | —      | —       |
| user_id    | VARCHAR(255) | ✓        | —           | —      | —       |
| role       | VARCHAR(50)  | ✓        | —           | —      | —       |

**outbox_events:**

| Column      | Data Type    | Not NULL | Primary Key | Unique | Default           |
|-------------|--------------|----------|-------------|--------|-------------------|
| id          | VARCHAR(255) | ✓        | ✓           | —      | —                 |
| routing_key | VARCHAR(255) | ✓        | —           | —      | —                 |
| payload     | TEXT         | ✓        | —           | —      | —                 |
| status      | VARCHAR(50)  | ✓        | —           | —      | —                 |
| created_at  | TIMESTAMPTZ  | —        | —           | —      | CURRENT_TIMESTAMP |
| retry_count | INTEGER      | —        | —           | —      | 0                 |

 
---

### 7.4. Comment Service

**comments:**

| Column          | Data Type | Required | Default       |
|-----------------|-----------|----------|---------------|
| id              | ObjectId  | ✓        | —             |
| taskId          | String    | ✓        | —             |
| userId          | String    | ✓        | —             |
| content         | String    | ✓        | —             |
| parentCommentId | String    | —        | —             |
| isEdited        | boolean   | —        | false         |
| createdAt       | Instant   | —        | Instant.now() |
| updatedAt       | Instant   | —        | —             |

 
---

### 7.5. Notification Service

*(Under development)*