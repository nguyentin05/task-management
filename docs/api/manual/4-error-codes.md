## 4. Error Codes

### 4.1. Error Code Structure

The Task Management system uses a unified error code set across all Microservices. Each error code is a 4-digit integer,
allowing the frontend and clients to catch and handle exceptions without relying on message strings.

Response structure:

```json
{
  "code": <code>,
  "message": <message>
}
```

### 4.2. Error Code Details

**Success:**

| Code | Enum Name | Message | HTTP Status |
|------|-----------|---------|-------------|
| 1000 | SUCCESS   | Success | 200 OK      |

**Failure:**

| Code | Enum Name                   | Message                                   | HTTP Status               |
|------|-----------------------------|-------------------------------------------|---------------------------|
| 1001 | UNAUTHENTICATED             | Not authenticated                         | 401 UNAUTHORIZED          |
| 1002 | ACCESS_DENIED               | Access denied                             | 403 FORBIDDEN             |
| 1003 | INVALID_CREDENTIALS         | Incorrect email or password               | 401 UNAUTHORIZED          |
| 1004 | ACCOUNT_DISABLED            | Account has been disabled                 | 403 FORBIDDEN             |
| 1005 | ACCOUNT_LOCKED              | Account has been locked                   | 403 FORBIDDEN             |
| 2001 | USER_EXISTED                | User already exists                       | 400 BAD REQUEST           |
| 2002 | ROLE_NOT_FOUND              | Role not found                            | 404 NOT FOUND             |
| 2003 | USER_NOT_FOUND              | User not found                            | 404 NOT FOUND             |
| 2004 | OLD_PASSWORD_INCORRECT      | Old password is incorrect                 | 400 BAD REQUEST           |
| 2005 | PROFILE_EXISTED             | Profile already exists                    | 400 BAD REQUEST           |
| 2006 | PROFILE_NOT_FOUND           | Profile not found                         | 404 NOT FOUND             |
| 3001 | FIELD_REQUIRED              | {field} is required                       | 400 BAD REQUEST           |
| 3002 | EMAIL_INVALID               | Invalid email format                      | 400 BAD REQUEST           |
| 3003 | PASSWORD_WEAK               | Password is not strong enough             | 400 BAD REQUEST           |
| 3004 | FIELD_SIZE_INVALID          | {field} must not exceed {max} characters  | 400 BAD REQUEST           |
| 3005 | INVALID_JSON                | Request body has invalid JSON format      | 400 BAD REQUEST           |
| 3006 | TYPE_MISMATCH               | Parameter {field} has wrong data type     | 400 BAD REQUEST           |
| 3007 | DOB_INVALID                 | Your age must be greater than {min}       | 400 BAD REQUEST           |
| 3008 | PHONE_INVALID               | Invalid phone number                      | 400 BAD REQUEST           |
| 3009 | TIME_INVALID                | End time must be after start time         | 400 BAD REQUEST           |
| 3010 | TIME_IN_PAST                | Time cannot be in the past                | 400 BAD REQUEST           |
| 3011 | LABEL_INVALID               | Invalid label                             | 400 BAD REQUEST           |
| 3012 | PROJECT_ROLE_INVALID        | Invalid project role                      | 400 BAD REQUEST           |
| 3013 | POSITION_INVALID            | Invalid position                          | 400 BAD REQUEST           |
| 3014 | FILE_INVALID                | Invalid file                              | 400 BAD REQUEST           |
| 4001 | ENDPOINT_NOT_FOUND          | Endpoint does not exist                   | 404 NOT FOUND             |
| 4002 | METHOD_NOT_ALLOWED          | HTTP method not supported                 | 405 METHOD_NOT_ALLOWED    |
| 5001 | INTERNAL_SERVER_ERROR       | An error occurred, please try again later | 500 INTERNAL_SERVER_ERROR |
| 5002 | SERVICE_UNAVAILABLE         | Service is temporarily unavailable        | 503 SERVICE UNAVAILABLE   |
| 6001 | WORKSPACE_NOT_FOUND         | Workspace not found                       | 404 NOT FOUND             |
| 6002 | PROJECT_NOT_FOUND           | Project not found                         | 404 NOT FOUND             |
| 6003 | PROJECT_NOT_IN_WORKSPACE    | Project does not belong to this workspace | 400 BAD REQUEST           |
| 6004 | USER_ALREADY_IN_PROJECT     | User is already a member of the project   | 400 BAD REQUEST           |
| 6005 | USER_NOT_IN_PROJECT         | User does not belong to this project      | 404 NOT FOUND             |
| 6006 | CANNOT_REMOVE_YOURSELF      | Cannot remove yourself from the project   | 400 BAD REQUEST           |
| 6007 | CANNOT_REMOVE_PROJECT_OWNER | Cannot remove the project owner           | 400 BAD REQUEST           |
| 7001 | COLUMN_NOT_FOUND            | Column not found                          | 404 NOT FOUND             |
| 7002 | COLUMN_NOT_IN_PROJECT       | Column does not belong to this project    | 400 BAD REQUEST           |
| 7003 | TASK_NOT_FOUND              | Task not found                            | 404 NOT FOUND             |
| 7004 | TASK_NOT_IN_COLUMN          | Task does not belong to this column       | 400 BAD REQUEST           |
| 7005 | USER_NOT_ASSIGNED           | User is not assigned to this task         | 400 BAD REQUEST           |
| 9001 | CANNOT_SEND_EMAIL           | Unable to send email                      | 400 BAD REQUEST           |