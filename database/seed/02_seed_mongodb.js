db = db.getSiblingDB('comment_db');
db.comments.insertMany([
    {
        taskId: "task-1",
        authorId: "alice-123",
        content: "Hãy cẩn thận khi dùng Eureka server nếu có nhiều instance nhé.",
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        taskId: "task-2",
        authorId: "bob-456",
        content: "Mình đang vẽ ERD trên PlantUML rồi.",
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        taskId: "task-4",
        authorId: "bob-456",
        content: "Đã handle thành công về CI cho Github Actions chưa?",
        createdAt: new Date(),
        updatedAt: new Date()
    }
]);