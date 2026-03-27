workspace "Task Management System" "A Trello clone application for task and project management." {
    !identifiers hierarchical
    model {
        user = person "User" "End User" {
            tags "People"
        }

        admin = person "Administrator" "Administrator" {
            tags "People"
        }

        taskManagement = softwareSystem "Task Management" "Task Management - Trello Clone" {
            tags "taskManagement"

            ui = container "UI" "Single Page Application" "React" {
                tags "ui"
            }

            apiGateway = container "API Gateway" "API Gateway" "Spring Cloud Gateway" {
                tags "apiGateway"
            }

            profileService = container "Profile Service" "Profile Service" "Spring Boot" {
                tags "service"

                profileSecurityFilterChain = component "Security Filter Chain" "Security Filter Chain" "Spring Security" {
                    tags "component"
                }

                profileController = component "Controller" "Controller" "Spring" {
                    tags "component"
                }

                profileServiceService = component "Service" "Service" "Spring" {
                    tags "component"
                }

                profileRepository = component "Repository" "Repository" "Spring" {
                    tags "component"
                }

                profileConsumer = component "Consumer" "Consumer" "Spring" {
                    tags "component"
                }

                profileSecurityFilterChain -> profileController
                profileController -> profileServiceService
                profileServiceService -> profileRepository
                profileConsumer -> profileServiceService
            }

            taskService = container "Task Service" "Task Service" "Spring Boot" {
                tags "service"

                taskSecurityFilterChain = component "Security Filter Chain" "Security Filter Chain" "Spring Security" {
                    tags "component"
                }

                taskController = component "Controller" "Controller" "Spring" {
                    tags "component"
                }

                taskServiceService = component "Service" "Service" "Spring" {
                    tags "component"
                }

                taskRepository = component "Repository" "Repository" "Spring" {
                    tags "component"
                }

                taskConsumer = component "Consumer" "Consumer" "Spring" {
                    tags "component"
                }

                taskProducer = component "Producer" "Producer" "Spring" {
                    tags "component"
                }

                taskScheduler = component "Scheduler" "Scheduler" "Spring" {
                    tags "component"
                }

                taskSecurityFilterChain -> taskController
                taskController -> taskServiceService
                taskServiceService -> taskRepository
                taskConsumer -> taskServiceService
                taskScheduler -> taskRepository
                taskScheduler -> taskProducer
            }

            authenticationService = container "Authentication Service" "Authentication Service" "Spring Boot" {
                tags "service"

                authenticationSecurityFilterChain = component "Security Filter Chain" "Security Filter Chain" "Spring Security" {
                    tags "component"
                }

                authenticationController = component "Controller" "Controller" "Spring" {
                    tags "component"
                }

                authenticationServiceService = component "Service" "Service" "Spring" {
                    tags "component"
                }

                authenticationRepository = component "Repository" "Repository" "Spring" {
                    tags "component"
                }

                authenticationScheduler = component "Scheduler" "Scheduler" "Spring" {
                    tags "component"
                }

                authenticationProducer = component "Producer" "Producer" "Spring" {
                    tags "component"
                }

                authenticationSecurityFilterChain -> authenticationController
                authenticationController -> authenticationServiceService
                authenticationServiceService -> authenticationRepository
                authenticationScheduler -> authenticationRepository
                authenticationScheduler -> authenticationProducer
            }

            commentService = container "Comment Service" "Comment Service" "Spring Boot" {
                tags "service"

                commentSecurityFilterChain = component "Security Filter Chain" "Security Filter Chain" "Spring Security" {
                    tags "component"
                }

                commentController = component "Controller" "Controller" "Spring" {
                    tags "component"
                }

                commentServiceService = component "Service" "Service" "Spring" {
                    tags "component"
                }

                commentRepository = component "Repository" "Repository" "Spring" {
                    tags "component"
                }

                commentConsumer = component "Consumer" "Consumer" "Spring" {
                    tags "component"
                }

                commentSecurityFilterChain -> commentController
                commentController -> commentServiceService
                commentServiceService -> commentRepository
                commentConsumer -> commentServiceService
            }

            notificationService = container "Notification Service" "Notification Service" "Spring Boot" {
                tags "notificationService"

                notificationConsumer = component "Consumer" "Consumer" "Spring" {
                    tags "component"
                }

                notificationServiceService = component "Service" "Service" "Spring" {
                    tags "component"
                }

                notificationRepositoryHttpclient = component "Repository Httpclient" "Repository Httpclient" "Openfeign" {
                    tags "component"
                }

                notificationConsumer -> notificationServiceService
                notificationServiceService -> notificationRepositoryHttpclient
            }

            messageBroker = container "Message Broker" "Message Broker" "RabbitMQ" {
                tags "messageBroker"
            }

            profileDb = container "Profile DB" "Graph DBMS" "Neo4j" {
                tags "database"
            }

            taskDb = container "Task DB" "RDBMS" "PostgreSQL" {
                tags "database"
            }

            authenticationDb = container "Authentication DB" "RDBMS" "PostgreSQL" {
                tags "database"
            }

            commentDb = container "Comment DB" "Document Store" "MongoDB" {
                tags "database"
            }

            ui -> apiGateway "Makes API requests to" "JSON/HTTP"

            apiGateway -> profileService "Forward"
            apiGateway -> taskService "Forward"
            apiGateway -> authenticationService "Forward"
            apiGateway -> commentService "Forward"

            profileService -> messageBroker "Subscribe event"
            profileService -> profileDb "Reads from and writes to" "Bolt"

            taskService -> messageBroker "Subscribe/Publish event"
            taskService -> taskDb "Reads from and writes to" "JDBC"

            taskService -> authenticationService "Fetch user info" "OpenFeign/HTTP"
            taskService -> profileService "Fetch profile info" "OpenFeign/HTTP"

            authenticationService -> messageBroker "Publish event"
            authenticationService -> authenticationDb "Reads from and writes to" "JDBC"

            commentService -> messageBroker "Subscribe event"
            commentService -> commentDb "Reads from and writes to" "MongoDB Wire Protocol"

            notificationService -> messageBroker "Subscribe event"

            apiGateway -> authenticationService.authenticationSecurityFilterChain "Forward"
            authenticationService.authenticationProducer -> messageBroker "Publish event"
            authenticationService.authenticationRepository -> authenticationDb "Reads from and writes to" "JDBC"

            apiGateway -> commentService.commentSecurityFilterChain "Forward"
            commentService.commentConsumer -> messageBroker "Subscribe event"
            commentService.commentRepository -> commentDb "Reads from and writes to" "MongoDB Wire Protocol"

            apiGateway -> taskManagement.profileService.profileSecurityFilterChain "Forward"
            taskManagement.profileService.profileConsumer -> messageBroker "Subscribe event"
            taskManagement.profileService.profileRepository -> profileDb "Reads from and writes to" "Bolt"

            apiGateway -> taskManagement.taskService.taskSecurityFilterChain "Forward"
            taskManagement.taskService.taskConsumer -> messageBroker "Subscribe event"
            taskManagement.taskService.taskProducer -> messageBroker "Publish event"
            taskManagement.taskService.taskRepository -> taskDb "Reads from and writes to" "JDBC"

            taskManagement.notificationService.notificationConsumer -> messageBroker "Subscribe event"
        }

        brevo = softwareSystem "Brevo" "SMTP" {
            tags "Brevo"
        }

        cloudinary = softwareSystem "Cloudinary" "Media Management SaaS" {
            tags "Cloudinary"
        }

        user -> taskManagement.ui "Add project, task, comment, ..."
        admin -> taskManagement.ui "Manage user, workspace, project, task, comment, ..."
        taskManagement.profileService -> cloudinary "Reads from and writes to" "HTTPS/REST"
        taskManagement.notificationService -> brevo "Reads from and writes to" "HTTPS/REST"
        taskManagement.profileService.profileServiceService -> cloudinary "Reads from and writes to" "HTTPS/REST"
        taskManagement.notificationService.notificationRepositoryHttpclient -> brevo "Reads from and writes to" "HTTPS/REST"
    }

    views {
        systemContext taskManagement "Context" {
            include *
            autoLayout tb
            title "System Context View: Task Management"
            description "The system context diagram for a Task Management System"
        }

        container taskManagement "Container" {
            include *
            autoLayout tb
            title "Container View: Task Management"
            description "The container diagram for a Task Management System"
        }

        component taskManagement.authenticationService "AuthenticationServiceComponent" {
            include *
            autoLayout tb
            title "Component View: Authentication Service"
            description "The component diagram for a Authentication Service"
        }

        component taskManagement.commentService "CommentServiceComponent" {
            include *
            autoLayout tb
            title "Component View: Comment Service"
            description "The component diagram for a Comment Service"
        }

        component taskManagement.notificationService "NotificationServiceComponent" {
            include *
            autoLayout tb
            title "Component View: Notification Service"
            description "The component diagram for a Notification Service"
        }

        component taskManagement.profileService "ProfileServiceComponent" {
            include *
            autoLayout tb
            title "Component View: Profile Service"
            description "The component diagram for a Profile Service"
        }

        component taskManagement.taskService "TaskServiceComponent" {
            include *
            autoLayout tb
            title "Component View: Task Service"
            description "The component diagram for a Task Service"
        }

        dynamic taskManagement "RegisterUser" "Luồng đăng ký tài khoản  User" {
            user -> taskManagement.ui "1. Submit form"
            taskManagement.ui -> taskManagement.apiGateway "2. POST /api/auth/register"
            taskManagement.apiGateway -> taskManagement.authenticationService "3. Forward request"
            taskManagement.authenticationService -> taskManagement.authenticationDb "4. Lưu thông tin User mới"
            taskManagement.authenticationService -> taskManagement.messageBroker "5. Publish 'user.created' event"

            taskManagement.profileService -> taskManagement.messageBroker "6. Subscribe 'user.created' event"
            taskManagement.profileService -> taskManagement.profileDb "7. Tạo default Profile"

            taskManagement.taskService -> taskManagement.messageBroker "8. Subscribe 'user.created' event"
            taskManagement.taskService -> taskManagement.taskDb "9. Tạo default Workspace"
            autoLayout
        }

        dynamic taskManagement "UpdateAvatar" "Luồng cập nhật ảnh đại diện lên Cloudinary" {
            user -> taskManagement.ui "1. Chọn ảnh và upload"
            taskManagement.ui -> taskManagement.apiGateway "2. POST /api/profiles/avatar"
            taskManagement.apiGateway -> taskManagement.profileService "3. Forward request"
            taskManagement.profileService -> cloudinary "4. Upload & get secure_url"
            taskManagement.profileService -> taskManagement.profileDb "5. Cập nhật URL vào Graph Node"
            autoLayout lr
        }

        dynamic taskManagement "SearchToInvite" "Luồng tìm kiếm User qua OpenFeign" {
            user -> taskManagement.ui "1. Gõ keyword (email/name)"
            taskManagement.ui -> taskManagement.apiGateway "2. GET /api/tasks/search-users"
            taskManagement.apiGateway -> taskManagement.taskService "3. Forward request"
            taskManagement.taskService -> taskManagement.authenticationService "4. Fetch Auth info (Feign Client)"
            taskManagement.taskService -> taskManagement.profileService "5. Fetch Profile info (Feign Client)"
            autoLayout lr
        }

        dynamic taskManagement "DeleteTask" "Luồng xóa Task và trigger xóa Comment qua Event" {
            user -> taskManagement.ui "1. Bấm xóa Task"
            taskManagement.ui -> taskManagement.apiGateway "2. DELETE /api/tasks/{id}"
            taskManagement.apiGateway -> taskManagement.taskService "3. Xóa Task trong DB"
            taskManagement.taskService -> taskManagement.taskDb "4. Xóa  Task"
            taskManagement.taskService -> taskManagement.messageBroker "5. Publish 'task.deleted' event"
            taskManagement.commentService -> taskManagement.messageBroker "6. Subscribe event"
            taskManagement.commentService -> taskManagement.commentDb "7. Xóa toàn bộ Comment của Task"
            autoLayout lr
        }

        dynamic taskManagement.taskService "GetAllColumns" "Luồng lấy danh sách Column kèm List Task" {
            taskManagement.apiGateway -> taskManagement.taskService.taskSecurityFilterChain "1. GET /api/projects/{id}/columns"
            taskManagement.taskService.taskSecurityFilterChain -> taskManagement.taskService.taskController "2. Forward request"
            taskManagement.taskService.taskController -> taskManagement.taskService.taskServiceService "3. getColumnsWithTasks(projectId)"
            taskManagement.taskService.taskServiceService -> taskManagement.taskService.taskRepository "4. Query joined data / Aggregation"
            taskManagement.taskService.taskRepository -> taskManagement.taskDb "5. SELECT columns, tasks..."
            autoLayout lr
        }

        styles {
            element "Element" {
                shape roundedbox
                color #000000
                background #ffffff
                strokeWidth 7
                fontSize 28
            }

            element "Person" {
                shape person
                stroke #4c8a1e
                color #4c8a1e
            }

            relationship "Relationship" {
                thickness 4
            }

            element "taskManagement" {
                stroke #005696
                color #005696
            }

            element "Brevo" {
                stroke #bf1e2e
                color #bf1e2e
            }

            element "Cloudinary" {
                stroke #f29100
                color #f29100
            }

            element "ui" {
                shape webBrowser
                stroke #005696
                color #005696
            }

            element "apiGateway" {
                stroke #005696
                color #005696
            }

            element "service" {
                stroke #005696
                color #005696
            }

            element "component" {
                stroke #005696
                color #005696
            }

            element "notificationService" {
                shape hexagon
                stroke #005696
                color #005696
            }

            element "messageBroker" {
                shape pipe
                stroke #005696
                color #005696
            }

            element "database" {
                shape cylinder
                stroke #005696
                color #005696
            }

            element "Boundary" {
                strokeWidth 5
            }
        }
    }
}
