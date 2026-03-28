# C4 Model

Sơ đồ kiến trúc hệ thống **Task Management** theo [C4 Model](https://c4model.com), được sinh từ **Structurizr DSL** và
export ra nhiều định dạng.

## Cấu trúc

```
c4/
├── structurizr/    # Source DSL (workspace.dsl) — nguồn sự thật duy nhất
├── drawio/         # Manual Design (.drawio)
├── images/         # Export sang SVG
├── mermaid/        # Export sang Mermaid (.mmd)
└── plantuml/       # Export sang PlantUML (.puml)
```

> **Nguồn sự thật:** [`structurizr/workspace.dsl`](./structurizr/workspace.dsl) — mọi chỉnh sửa kiến trúc đều thực hiện
> ở đây, các định dạng khác được export ra từ file này.

## Các sơ đồ

### Context Diagram

Mô tả hệ thống Task Management trong môi trường tổng thể, bao gồm người dùng và các hệ thống bên ngoài.

![Context](./images/structurizr-Context.svg)

### Container Diagram

Mô tả các service, database và cách chúng giao tiếp với nhau.

![Container](./images/structurizr-Container.svg)

### Component Diagrams

Chi tiết bên trong từng service:

| Service                | Sơ đồ                                                                      |
|------------------------|----------------------------------------------------------------------------|
| Authentication Service | ![Authentication](./images/structurizr-AuthenticationServiceComponent.svg) |
| Profile Service        | ![Profile](./images/structurizr-ProfileServiceComponent.svg)               |
| Task Service           | ![Task](./images/structurizr-TaskServiceComponent.svg)                     |
| Comment Service        | ![Comment](./images/structurizr-CommentServiceComponent.svg)               |
| Notification Service   | ![Notification](./images/structurizr-NotificationServiceComponent.svg)     |

## Công cụ

- [Structurizr Lite](https://structurizr.com/help/lite) — render và chỉnh sửa DSL
- [draw.io](https://app.diagrams.net) — mở file `.drawio`