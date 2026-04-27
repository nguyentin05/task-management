## 6. Endpoint List

| Service | Method | API Endpoint                                     | Description                                |
|---------|--------|--------------------------------------------------|--------------------------------------------|
| Auth    | POST   | `/auth/token`                                    | Authenticate user and issue token          |
| Auth    | POST   | `/auth/refresh`                                  | Refresh token                              |
| Auth    | POST   | `/internal/auth/introspect`                      | Inspect token                              |
| Auth    | GET    | `/internal/auth/users/search`                    | Search users                               |
| Auth    | POST   | `/auth/logout`                                   | Log out                                    |
| Auth    | POST   | `/auth/users/register`                           | Register a user account                    |
| Auth    | GET    | `/auth/roles`                                    | Get all roles in the system                |
| Auth    | POST   | `/auth/users`                                    | Create a user                              |
| Auth    | GET    | `/auth/users`                                    | Get all users                              |
| Auth    | GET    | `/auth/users/{userId}`                           | Get user details                           |
| Auth    | PUT    | `/auth/users/{userId}/reset-password`            | Reset user password                        |
| Auth    | PUT    | `/auth/users/{userId}/roles`                     | Change system role for a user              |
| Auth    | GET    | `/auth/users/me`                                 | Get current user info                      |
| Auth    | PUT    | `/auth/users/me/change-password`                 | Change current user password               |
| Auth    | DELETE | `/auth/users/{userId}`                           | Delete a user                              |
| Profile | GET    | `/internal/profiles/search`                      | Search profiles                            |
| Profile | GET    | `/profiles/me`                                   | Get current user profile                   |
| Profile | PATCH  | `/profiles/me`                                   | Update current user profile                |
| Profile | GET    | `/profiles`                                      | Get all profiles                           |
| Profile | GET    | `/profiles/{profileId}`                          | Get profile details                        |
| Profile | PATCH  | `/profiles/{profileId}`                          | Update a profile                           |
| Profile | PUT    | `/profiles/me/avatar`                            | Update current user avatar                 |
| Profile | PUT    | `/profiles/{profileId}/avatar`                   | Update avatar by profile ID                |
| Task    | GET    | `/workspaces/me`                                 | Get current user workspace                 |
| Task    | GET    | `/workspaces/me/projects`                        | Get projects in current user workspace     |
| Task    | PATCH  | `/workspaces/me`                                 | Update current user workspace              |
| Task    | DELETE | `/workspaces/me/projects/{projectId}`            | Remove project from current user workspace |
| Task    | GET    | `/workspaces`                                    | Get all workspaces                         |
| Task    | GET    | `/workspaces/{workspaceId}`                      | Get workspace details                      |
| Task    | GET    | `/workspaces/{workspaceId}/projects`             | Get projects in a workspace                |
| Task    | PATCH  | `/workspaces/{workspaceId}`                      | Update a workspace                         |
| Task    | DELETE | `/workspaces/{workspaceId}/projects/{projectId}` | Remove project from workspace              |
| Task    | POST   | `/projects`                                      | Create a project                           |
| Task    | GET    | `/projects`                                      | Get all projects                           |
| Task    | GET    | `/projects/{projectId}`                          | Get project details                        |
| Task    | PATCH  | `/projects/{projectId}`                          | Update a project                           |
| Task    | DELETE | `/projects/{projectId}`                          | Delete a project                           |
| Task    | GET    | `/projects/{projectId}/members`                  | Get project members                        |
| Task    | POST   | `/projects/{projectId}/members`                  | Add member to project                      |
| Task    | GET    | `/projects/{projectId}/members/search`           | Search members                             |
| Task    | GET    | `/projects/{projectId}/statistics`               | Get project statistics                     |
| Task    | PUT    | `/projects/{projectId}/members/{userId}`         | Change member role                         |
| Task    | DELETE | `/projects/{projectId}/members/{userId}`         | Remove member from project                 |
| Task    | POST   | `/projects/{projectId}/columns`                  | Create a new column                        |
| Task    | GET    | `/projects/{projectId}/columns`                  | View Kanban board                          |
| Task    | PATCH  | `/projects/{projectId}/columns/{columnId}`       | Rename / reorder column                    |
| Task    | DELETE | `/projects/{projectId}/columns/{columnId}`       | Delete a column                            |
| Task    | POST   | `/columns/{columnId}/tasks`                      | Create a new task                          |
| Task    | GET    | `/tasks/{taskId}`                                | Get task details                           |
| Task    | PATCH  | `/tasks/{taskId}`                                | Update task details                        |
| Task    | PUT    | `/tasks/{taskId}/move`                           | Move a task                                |
| Task    | POST   | `/tasks/{taskId}/assignees`                      | Assign a user to a task                    |
| Task    | DELETE | `/tasks/{taskId}/assignees/{userId}`             | Remove a user from a task                  |
| Task    | DELETE | `/tasks/{taskId}`                                | Delete a task                              |
| Comment | POST   | `/tasks/{taskId}/comments`                       | Create a new comment                       |
| Comment | GET    | `/tasks/{taskId}/comments`                       | Get comments for a task                    |
| Comment | PUT    | `/comments/{commentId}`                          | Edit a comment                             |
| Comment | DELETE | `/comments/{commentId}`                          | Delete a comment                           |

---

## 6.1. Authentication Service

### `POST /auth/token` — Authenticate User and Issue Token

- **Description:** Authenticate user and issue token
- **Role:** None
- **Scope:** External

**Request Body:**

| Field    | Type   | Required | Validation                              | Description   |
|----------|--------|----------|-----------------------------------------|---------------|
| email    | String | Yes      | `@Email`, `@NotBlank`, `@Size(max=255)` | User email    |
| password | String | Yes      | `@NotBlank`, `@Size(max=255)`           | User password |

**Response Body:**

| Field                | Type                   | Description          |
|----------------------|------------------------|----------------------|
| code                 | Integer                | Result code          |
| message              | String                 | Accompanying message |
| result               | AuthenticationResponse | Authentication info  |
| result.token         | String                 | Authentication token |
| result.authenticated | boolean                | Authentication flag  |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "token": "<token>",
    "authenticated": <authenticated>
  }
}
```

---

### `POST /auth/refresh` — Refresh Token

- **Description:** Issue a new token if the refresh token is still valid
- **Role:** None
- **Scope:** External

**Request Body:**

| Field | Type   | Required | Validation  | Description          |
|-------|--------|----------|-------------|----------------------|
| token | String | Yes      | `@NotBlank` | Authentication token |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "token": "<token>",
    "authenticated": <authenticated>
  }
}
```

---

### `POST /internal/auth/introspect` — Inspect Token

- **Description:** Inspect a token
- **Role:** None
- **Scope:** Internal

**Request Body:**

| Field | Type   | Required | Validation  | Description          |
|-------|--------|----------|-------------|----------------------|
| token | String | Yes      | `@NotBlank` | Authentication token |

**Response Body:**

| Field        | Type               | Description          |
|--------------|--------------------|----------------------|
| code         | Integer            | Result code          |
| message      | String             | Accompanying message |
| result       | IntrospectResponse | Inspection info      |
| result.valid | boolean            | Token validity flag  |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "valid": <valid>
  }
}
```

---

### `GET /internal/auth/users/search` — Search Users

- **Description:** Search a list of users by email
- **Role:** None
- **Scope:** Internal

**Query Parameters:**

| Name    | Type           | Required | Description        | Default |
|---------|----------------|----------|--------------------|---------|
| email   | String         | No       | Email to search    | None    |
| userIds | List\<String\> | No       | User IDs to search | None    |

**Success Response:**

```json
{
  "code": 1000,
  "result": [
    ...
  ]
}
```

---

### `POST /auth/logout` — Log Out

- **Description:** Revoke the token and add it to the blacklist
- **Role:** None
- **Scope:** External

**Request Body:**

| Field | Type   | Required | Validation  | Description          |
|-------|--------|----------|-------------|----------------------|
| token | String | Yes      | `@NotBlank` | Authentication token |

**Success Response:**

```json
{
  "code": 1000,
  "message": "Logged out successfully"
}
```

---

### `POST /auth/users/register` — Register User Account

- **Description:** Register a new user account
- **Role:** None
- **Scope:** External

**Request Body:**

| Field     | Type   | Required | Validation                                           | Description |
|-----------|--------|----------|------------------------------------------------------|-------------|
| email     | String | Yes      | `@NotBlank`, `@Email`, `@Size(max=255)`              | Email       |
| password  | String | Yes      | `@NotBlank`, `@Size(max=255)`, `@PasswordConstraint` | Password    |
| firstName | String | Yes      | `@NotBlank`, `@Size(max=255)`                        | First name  |
| lastName  | String | Yes      | `@NotBlank`, `@Size(max=255)`                        | Last name   |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "email": "<email>",
    "roles": [
      ...
    ]
  }
}
```

---

### `GET /auth/roles` — Get All Roles

- **Description:** Get all roles in the system
- **Role:** Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 5       |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "data": [
      {
        "name": "<name>",
        "description": "<description>"
      }
    ]
  }
}
```

---

### `POST /auth/users` — Create User

- **Description:** Create a new user
- **Role:** Admin
- **Scope:** External

**Request Body:**

| Field     | Type          | Required | Validation                              | Description |
|-----------|---------------|----------|-----------------------------------------|-------------|
| email     | String        | Yes      | `@NotBlank`, `@Email`, `@Size(max=255)` | Email       |
| password  | String        | Yes      | `@NotBlank`, `@Size(max=255)`           | Password    |
| firstName | String        | Yes      | `@NotBlank`, `@Size(max=255)`           | First name  |
| lastName  | String        | Yes      | `@NotBlank`, `@Size(max=255)`           | Last name   |
| roles     | Set\<String\> | No       | —                                       | Roles       |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "email": "<email>",
    "roles": [
      ...
    ]
  }
}
```

---

### `GET /auth/users` — Get All Users

- **Description:** Get all users
- **Role:** Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 20      |

---

### `GET /auth/users/{userId}` — Get User Details

- **Description:** Get detailed information of a user by userId
- **Role:** Admin
- **Scope:** External

**Path Parameters:**

| Name     | Type   | Description |
|----------|--------|-------------|
| {userId} | String | User ID     |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "email": "<email>",
    "roles": [
      ...
    ]
  }
}
```

---

### `PUT /auth/users/{userId}/reset-password` — Reset User Password

- **Description:** Reset the password of a user by userId
- **Role:** Admin
- **Scope:** External

**Request Body:**

| Field       | Type   | Required | Validation                                           | Description  |
|-------------|--------|----------|------------------------------------------------------|--------------|
| newPassword | String | Yes      | `@NotBlank`, `@Size(max=255)`, `@PasswordConstraint` | New password |

**Success Response:**

```json
{
  "code": 1000,
  "message": "User password reset successfully"
}
```

---

### `PUT /auth/users/{userId}/roles` — Change System Role for User

- **Description:** Change the system role of a user by userId
- **Role:** Admin
- **Scope:** External

**Request Body:**

| Field | Type          | Required | Validation  | Description   |
|-------|---------------|----------|-------------|---------------|
| roles | Set\<String\> | Yes      | `@NotEmpty` | List of roles |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "email": "<email>",
    "roles": [
      ...
    ]
  }
}
```

---

### `GET /auth/users/me` — Get Current User Info

- **Description:** Get detailed information of the currently logged-in user
- **Role:** User / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "email": "<email>",
    "roles": [
      ...
    ]
  }
}
```

---

### `PUT /auth/users/me/change-password` — Change Current User Password

- **Description:** Update the password of the currently logged-in user
- **Role:** User / Admin
- **Scope:** External

**Request Body:**

| Field       | Type   | Required | Validation                                           | Description      |
|-------------|--------|----------|------------------------------------------------------|------------------|
| oldPassword | String | Yes      | `@NotBlank`                                          | Current password |
| newPassword | String | Yes      | `@NotBlank`, `@Size(max=255)`, `@PasswordConstraint` | New password     |

**Success Response:**

```json
{
  "code": 1000,
  "message": "Password updated successfully"
}
```

---

### `DELETE /auth/users/{userId}` — Delete User

- **Description:** Delete a specific user by ID
- **Role:** Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "message": "User deleted successfully"
}
```

---

## 6.2. Profile Service

### `GET /internal/profiles/search` — Search Profiles

- **Description:** Search profiles
- **Role:** None
- **Scope:** Internal

**Query Parameters:**

| Name    | Type           | Required | Description                | Default |
|---------|----------------|----------|----------------------------|---------|
| userIds | List\<String\> | Yes      | List of user IDs to search | None    |

**Success Response:**

```json
{
  "code": 1000,
  "result": [
    {
      "userId": "<userId>",
      "firstName": "<firstName>",
      "lastName": "<lastName>",
      "avatar": "<avatar>"
    }
  ]
}
```

---

### `GET /profiles/me` — Get Current User Profile

- **Description:** Get the profile of the currently logged-in user
- **Role:** User / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "userId": "<userId>",
    "firstName": "<firstName>",
    "lastName": "<lastName>",
    "dob": "<dob>",
    "phoneNumber": "<phoneNumber>",
    "avatar": "<avatar>"
  }
}
```

---

### `PATCH /profiles/me` — Update Current User Profile

- **Description:** Update the profile of the currently logged-in user
- **Role:** User / Admin
- **Scope:** External

**Request Body:**

| Field       | Type      | Required | Validation               | Description   |
|-------------|-----------|----------|--------------------------|---------------|
| firstName   | String    | No       | `@Size(max=255)`         | First name    |
| lastName    | String    | No       | `@Size(max=255)`         | Last name     |
| dob         | LocalDate | No       | `@DobConstraint`         | Date of birth |
| phoneNumber | String    | No       | `@PhoneNumberConstraint` | Phone number  |

---

### `GET /profiles` — Get All Profiles

- **Description:** Get all profiles
- **Role:** Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 20      |

---

### `GET /profiles/{profileId}` — Get Profile Details

- **Description:** Get detailed information of a specific profile by profileId
- **Role:** Admin
- **Scope:** External

---

### `PATCH /profiles/{profileId}` — Update Profile

- **Description:** Update a profile by profileId
- **Role:** Admin
- **Scope:** External

**Request Body:**

| Field       | Type      | Required | Validation               | Description   |
|-------------|-----------|----------|--------------------------|---------------|
| firstName   | String    | No       | `@Size(max=255)`         | First name    |
| lastName    | String    | No       | `@Size(max=255)`         | Last name     |
| dob         | LocalDate | No       | `@DobConstraint`         | Date of birth |
| phoneNumber | String    | No       | `@PhoneNumberConstraint` | Phone number  |

---

### `PUT /profiles/me/avatar` — Update Current User Avatar

- **Description:** Update the avatar of the currently logged-in user
- **Role:** User / Admin
- **Scope:** External
- **Request Header:** `Content-Type: multipart/form-data`

**Request Body:**

| Field  | Type          | Required | Validation                    | Description       |
|--------|---------------|----------|-------------------------------|-------------------|
| avatar | MultipartFile | Yes      | `@NotNull`, `@FileConstraint` | Avatar image file |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "avatar": "<avatar>"
  }
}
```

---

### `PUT /profiles/{profileId}/avatar` — Update Profile Avatar

- **Description:** Update avatar by profileId
- **Role:** Admin
- **Scope:** External
- **Request Header:** `Content-Type: multipart/form-data`

**Request Body:**

| Field  | Type          | Required | Validation                    | Description       |
|--------|---------------|----------|-------------------------------|-------------------|
| avatar | MultipartFile | Yes      | `@NotNull`, `@FileConstraint` | Avatar image file |

---

## 6.3. Task Service

### `GET /workspaces/me` — Get Current User Workspace

- **Description:** Get the workspace of the currently logged-in user
- **Role:** User / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "name": "<name>",
    "description": "<description>"
  }
}
```

---

### `GET /workspaces/me/projects` — Get Projects in Current User Workspace

- **Description:** Get the list of projects in the current user's workspace
- **Role:** User
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 20      |

---

### `PATCH /workspaces/me` — Update Current User Workspace

- **Description:** Update the name or description of the current user's workspace
- **Role:** User / Admin
- **Scope:** External

**Request Body:**

| Field       | Type   | Required | Validation       | Description           |
|-------------|--------|----------|------------------|-----------------------|
| name        | String | No       | `@Size(max=255)` | Workspace name        |
| description | String | No       | —                | Workspace description |

---

### `DELETE /workspaces/me/projects/{projectId}` — Remove Project from Current Workspace

- **Description:** Remove a project from the current user's workspace or leave a project
- **Role:** User / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "message": "Project removed from workspace successfully"
}
```

---

### `GET /workspaces` — Get All Workspaces

- **Description:** Get all workspaces in the system
- **Role:** Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 10      |

---

### `GET /workspaces/{workspaceId}` — Get Workspace Details

- **Description:** Get detailed information of a specific workspace by workspaceId
- **Role:** Admin
- **Scope:** External

---

### `GET /workspaces/{workspaceId}/projects` — Get Projects in a Workspace

- **Description:** Get the list of projects in a specific workspace
- **Role:** Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 10      |

---

### `PATCH /workspaces/{workspaceId}` — Update a Workspace

- **Description:** Update the name or description of a workspace by ID
- **Role:** Admin
- **Scope:** External

**Request Body:**

| Field       | Type   | Required | Validation       | Description           |
|-------------|--------|----------|------------------|-----------------------|
| name        | String | No       | `@Size(max=255)` | Workspace name        |
| description | String | No       | —                | Workspace description |

---

### `DELETE /workspaces/{workspaceId}/projects/{projectId}` — Remove Project from Workspace

- **Description:** Remove a project from a workspace
- **Role:** Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "message": "Project removed from workspace successfully"
}
```

---

### `POST /projects` — Create New Project

- **Description:** Create a new project and automatically link it to the creator's workspace
- **Role:** User / Admin
- **Scope:** External

**Request Body:**

| Field       | Type    | Required | Validation                     | Description         |
|-------------|---------|----------|--------------------------------|---------------------|
| name        | String  | Yes      | `@NotBlank`, `@Size(max=255)`  | Project name        |
| description | String  | No       | —                              | Project description |
| startAt     | Instant | Yes      | `@NotNull`, `@FutureOrPresent` | Project start time  |
| endAt       | Instant | Yes      | `@NotNull`, `@TimeConstraint`  | Project end time    |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "name": "<name>",
    "description": "<description>",
    "createdBy": "<createdBy>",
    "startAt": "<startAt>",
    "endAt": "<endAt>",
    "createdAt": "<createdAt>",
    "updatedAt": "<updatedAt>"
  }
}
```

---

### `GET /projects` — Get All Projects

- **Description:** Get all projects in the system
- **Role:** Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 10      |

---

### `GET /projects/{projectId}` — Get Project Details

- **Description:** Get detailed information of a project by ID
- **Role:** User / Admin
- **Scope:** External

---

### `PATCH /projects/{projectId}` — Update a Project

- **Description:** Update project information by ID
- **Role:** User - Manager / Admin
- **Scope:** External

**Request Body:**

| Field       | Type    | Required | Validation                            | Description         |
|-------------|---------|----------|---------------------------------------|---------------------|
| name        | String  | No       | `@Size(max=255)`                      | Project name        |
| description | String  | No       | —                                     | Project description |
| startAt     | Instant | No       | `@FutureOrPresent`, `@TimeConstraint` | Start time          |
| endAt       | Instant | No       | `@TimeConstraint`                     | End time            |

---

### `DELETE /projects/{projectId}` — Delete Project

- **Description:** Delete a project by ID
- **Role:** User - Manager / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "message": "Project deleted successfully"
}
```

---

### `GET /projects/{projectId}/members` — Get Project Members

- **Description:** Get the list of members currently in the project
- **Role:** User / Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 20      |

---

### `GET /projects/{projectId}/members/search` — Search Project Members

- **Description:** Search users by email to invite to the project
- **Role:** User - Manager / Admin
- **Scope:** External

**Query Parameters:**

| Name  | Type   | Required | Description     | Default |
|-------|--------|----------|-----------------|---------|
| email | String | Yes      | Email to search | None    |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "userId": "<userId>",
    "email": "<email>",
    "firstName": "<firstName>",
    "lastName": "<lastName>",
    "avatar": "<avatar>",
    "alreadyMember": <alreadyMember>
  }
}
```

---

### `POST /projects/{projectId}/members` — Add Member to Project

- **Description:** Add a new member to a project
- **Role:** User - Manager / Admin
- **Scope:** External

**Request Body:**

| Field  | Type   | Required | Validation  | Description         |
|--------|--------|----------|-------------|---------------------|
| userId | String | Yes      | `@NotBlank` | User ID             |
| role   | String | Yes      | `@NotNull`  | Role in the project |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "userId": "<userId>",
    "role": "<role>"
  }
}
```

---

### `PUT /projects/{projectId}/members/{userId}` — Change Member Role in Project

- **Description:** Change the role of a member in a project
- **Role:** User - Manager / Admin
- **Scope:** External

**Request Body:**

| Field | Type        | Required | Validation | Description |
|-------|-------------|----------|------------|-------------|
| role  | ProjectRole | Yes      | `@NotNull` | New role    |

**Success Response:**

```json
{
  "code": 1000,
  "message": "Member role updated successfully"
}
```

---

### `DELETE /projects/{projectId}/members/{userId}` — Remove Member from Project

- **Description:** Remove a member from a project
- **Role:** User - Manager / Admin
- **Scope:** External

---

### `POST /projects/{projectId}/columns` — Create Column in Project

- **Description:** Create a new column in a project
- **Role:** User - Manager / Admin
- **Scope:** External

**Request Body:**

| Field    | Type   | Required | Validation                     | Description      |
|----------|--------|----------|--------------------------------|------------------|
| name     | String | Yes      | `@NotBlank`, `@Size(max=255)`  | Column name      |
| position | Double | Yes      | `@NotNull`, `@DecimalMin(0.0)` | Display position |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "projectId": "<projectId>",
    "name": "<name>",
    "position": <position>,
    "createdAt": <createdAt>,
    "updatedAt": <updatedAt>,
    "columnTaskResponses": [
      ...
    ]
  }
}
```

---

### `GET /projects/{projectId}/columns` — View Kanban Board

- **Description:** Get the list of columns in a project, including summarized task lists
- **Role:** User / Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 10      |

---

### `PATCH /projects/{projectId}/columns/{columnId}` — Update Column

- **Description:** Rename a column or change its position on the board
- **Role:** User - Manager / Admin
- **Scope:** External

**Request Body:**

| Field    | Type   | Required | Validation         | Description      |
|----------|--------|----------|--------------------|------------------|
| name     | String | No       | `@Size(max=255)`   | Column name      |
| position | Double | No       | `@DecimalMin(0.0)` | Display position |

---

### `DELETE /projects/{projectId}/columns/{columnId}` — Delete Column

- **Description:** Delete a column from a project
- **Role:** User - Manager / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "message": "Column deleted successfully"
}
```

---

### `POST /columns/{columnId}/tasks` — Create New Task

- **Description:** Create a new task within a specific column
- **Role:** User - Manager / Admin
- **Scope:** External

**Request Body:**

| Field       | Type      | Required | Validation                                        | Description      |
|-------------|-----------|----------|---------------------------------------------------|------------------|
| title       | String    | Yes      | `@NotBlank`, `@Size(max=255)`                     | Task title       |
| description | String    | No       | —                                                 | Description      |
| position    | Double    | Yes      | `@NotNull`, `@DecimalMin(0.0)`                    | Display position |
| startAt     | Instant   | Yes      | `@NotNull`, `@FutureOrPresent`, `@TimeConstraint` | Start time       |
| dueAt       | Instant   | Yes      | `@NotNull`, `@TimeConstraint`                     | Due date         |
| label       | TaskLabel | Yes      | `@NotNull`                                        | Label            |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "columnId": "<columnId>",
    "title": "<title>",
    "description": "<description>",
    "position": "<position>",
    "startAt": "<startAt>",
    "dueAt": "<dueAt>",
    "assigneeId": "<assigneeId>",
    "label": "<label>",
    "createdBy": "<createdBy>",
    "createdAt": "<createdAt>",
    "updatedAt": "<updatedAt>",
    "completedAt": "<completedAt>"
  }
}
```

---

### `GET /columns/{columnId}/tasks/{taskId}` — Get Task Details

- **Description:** Get detailed information of a task by columnId and taskId
- **Role:** User / Admin
- **Scope:** External

---

### `PATCH /tasks/{taskId}` — Update Task

- **Description:** Update task details
- **Role:** User / Admin
- **Scope:** External

**Request Body:**

| Field       | Type      | Required | Validation                            | Description |
|-------------|-----------|----------|---------------------------------------|-------------|
| title       | String    | No       | `@Size(max=255)`                      | Task title  |
| description | String    | No       | —                                     | Description |
| startAt     | Instant   | No       | `@FutureOrPresent`, `@TimeConstraint` | Start time  |
| dueAt       | Instant   | No       | `@TimeConstraint`                     | Due date    |
| label       | TaskLabel | No       | —                                     | Label color |

---

### `PUT /tasks/{taskId}/move` — Move Task

- **Description:** Move a task between columns
- **Role:** User / Admin
- **Scope:** External

**Request Body:**

| Field    | Type   | Required | Validation                     | Description                   |
|----------|--------|----------|--------------------------------|-------------------------------|
| columnId | String | Yes      | `@NotBlank`                    | Destination column ID         |
| position | Double | Yes      | `@NotNull`, `@DecimalMin(0.0)` | Average value of new position |

---

### `DELETE /tasks/{taskId}` — Delete Task

- **Description:** Delete a task
- **Role:** User - Manager / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "message": "Task deleted successfully"
}
```

---

### `POST /tasks/{taskId}/assignees` — Assign User to Task

- **Description:** Assign a user to a task
- **Role:** User - Manager / Admin
- **Scope:** External

**Request Body:**

| Field  | Type   | Required | Validation  | Description              |
|--------|--------|----------|-------------|--------------------------|
| userId | String | Yes      | `@NotBlank` | ID of the user to assign |

---

### `DELETE /tasks/{taskId}/assignees/{userId}` — Remove User from Task

- **Description:** Remove a member from a task
- **Role:** User - Manager / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "message": "Member removed from task successfully"
}
```

---

### `GET /projects/{projectId}/statistics` — Project Statistics

- **Description:** Get overall project metrics for dashboard charts
- **Role:** User / Admin
- **Scope:** External

**Response Body:**

| Field                 | Type    | Description               |
|-----------------------|---------|---------------------------|
| result.projectId      | String  | Project ID                |
| result.projectName    | String  | Project name              |
| result.totalTasks     | Integer | Total number of tasks     |
| result.completedTasks | Integer | Number of completed tasks |
| result.completionRate | Double  | Completion rate           |
| result.totalMembers   | Integer | Total number of members   |
| result.totalColumns   | Integer | Total number of columns   |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "projectId": "<projectId>",
    "projectName": "<projectName>",
    "totalTasks": <totalTasks>,
    "completedTasks": <completedTasks>,
    "completionRate": <completionRate>,
    "totalMembers": <totalMembers>,
    "totalColumns": <totalColumns>
  }
}
```

---

## 6.4. Comment Service

### `GET /tasks/{taskId}/comments` — Get Comment List

- **Description:** Get the list of comments for a task
- **Role:** User / Admin
- **Scope:** External

**Query Parameters:**

| Name | Type    | Required | Description       | Default |
|------|---------|----------|-------------------|---------|
| page | Integer | No       | Current page      | 1       |
| size | Integer | No       | Elements per page | 10      |

---

### `POST /tasks/{taskId}/comments` — Create New Comment

- **Description:** Add a comment to a task
- **Role:** User / Admin
- **Scope:** External

**Request Body:**

| Field           | Type   | Required | Validation  | Description       |
|-----------------|--------|----------|-------------|-------------------|
| content         | String | Yes      | `@NotBlank` | Comment content   |
| parentCommentId | String | No       | —           | Parent comment ID |

**Success Response:**

```json
{
  "code": 1000,
  "result": {
    "id": "<id>",
    "taskId": "<taskId>",
    "userId": "<userId>",
    "isEdited": <isEdited>,
    "content": "<content>",
    "parentCommentId": "<parentCommentId>",
    "createdAt": "<createdAt>"
  }
}
```

---

### `PUT /comments/{commentId}` — Edit Comment

- **Description:** Update the content of a comment
- **Role:** User / Admin
- **Scope:** External

**Request Body:**

| Field   | Type   | Required | Validation  | Description     |
|---------|--------|----------|-------------|-----------------|
| content | String | Yes      | `@NotBlank` | Comment content |

---

### `DELETE /comments/{commentId}` — Delete Comment

- **Description:** Delete a comment
- **Role:** User / Admin
- **Scope:** External

**Success Response:**

```json
{
  "code": 1000,
  "message": "Comment deleted successfully"
}
```