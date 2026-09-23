| Method | Endpoint                      | Access        | Purpose                      |
| ------ | ----------------------------- | ------------- | ---------------------------- |
| POST   | `/api/auth/register`          | Public        | Register citizen             |
| POST   | `/api/auth/login`             | Public        | Login                        |
| GET    | `/api/users/me`               | Authenticated | Current user                 |
| GET    | `/api/users`                  | ADMIN         | List users                   |
| GET    | `/api/categories`             | Authenticated | List categories              |
| POST   | `/api/categories`             | ADMIN         | Create category              |
| PUT    | `/api/categories/{id}`        | ADMIN         | Update category              |
| DELETE | `/api/categories/{id}`        | ADMIN         | Delete/deactivate category   |
| GET    | `/api/departments`            | Authenticated | List departments             |
| POST   | `/api/departments`            | ADMIN         | Create department            |
| PUT    | `/api/departments/{id}`       | ADMIN         | Update department            |
| DELETE | `/api/departments/{id}`       | ADMIN         | Delete/deactivate department |
| POST   | `/api/issues`                 | CITIZEN       | Create issue                 |
| GET    | `/api/issues/{id}`            | Authorized    | Get issue                    |
| GET    | `/api/issues/my`              | CITIZEN       | Citizen's issues             |
| GET    | `/api/issues`                 | OFFICER/ADMIN | Authority issue list         |
| PATCH  | `/api/issues/{id}/department` | ADMIN         | Assign department            |
| PATCH  | `/api/issues/{id}/assign`     | ADMIN         | Assign officer               |
| PATCH  | `/api/issues/{id}/status`     | OFFICER/ADMIN | Change status                |
| GET    | `/api/issues/{id}/history`    | Authorized    | Status history               |
| PATCH  | `/api/issues/{id}/verify`     | CITIZEN       | Verify resolution            |
| PATCH  | `/api/issues/{id}/reopen`     | CITIZEN       | Reopen issue                 |
