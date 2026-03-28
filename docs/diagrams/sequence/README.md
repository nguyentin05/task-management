# Sequence Diagrams

Sơ đồ tuần tự mô tả luồng tương tác giữa các thành phần trong hệ thống **Task Management**.

## Cấu trúc

```
sequence/
├── images/     # SVG render từ Structurizr
├── mermaid/    # Mermaid (.mmd)
├── plantuml/   # PlantUML (.puml)
└── wsd/        # WebSequenceDiagrams (.wsd)
```

> **Nguồn sự thật:** [
`../../../architecture/c4/structurizr/workspace.dsl`](../../architecture/c4/structurizr/workspace.dsl) — các sơ đồ
> sequence được định nghĩa trong Structurizr DSL và export ra các định dạng trên.

## Danh sách sơ đồ

| Sơ đồ                                                       | Mô tả                                                 |
|-------------------------------------------------------------|-------------------------------------------------------|
| [Register User](./images/structurizr-RegisterUser.svg)      | Luồng đăng ký tài khoản mới                           |
| [Get All Columns](./images/structurizr-GetAllColumns.svg)   | Luồng lấy danh sách cột bao gồm cả task trong project |
| [Delete Task](./images/structurizr-DeleteTask.svg)          | Luồng xóa task và các side effects                    |
| [Search To Invite](./images/structurizr-SearchToInvite.svg) | Luồng tìm kiếm và mời thành viên                      |
| [Update Avatar](./images/structurizr-UpdateAvatar.svg)      | Luồng cập nhật ảnh đại diện                           |

## Xem sơ đồ

- **SVG** — xem trực tiếp trên Repository
- **Mermaid** — [mermaid.live](https://mermaid.live)
- **PlantUML** — [plantuml.com](https://www.plantuml.com/plantuml)
- **WSD** — [websequencediagrams.com](https://www.websequencediagrams.com)