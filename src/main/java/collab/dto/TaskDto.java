package collab.dto;

public class TaskDto {

    // ===== RESPONSE DTO =====
    public static class TaskResponse {
        public Long id;
        public Long groupId;
        public Long createdById;
        public String title;
        public String description;
        public String status;
        public String deadline;

        public TaskResponse() {}

        public Long getId() { return id; }
        public Long getGroupId() { return groupId; }
        public Long getCreatedById() { return createdById; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public String getDeadline() { return deadline; }
    }

    // ===== REQUEST DTO =====
    public static class CreateTaskRequest {
        public Long groupId;
        public Long createdById;
        public String title;
        public String description;
        public String deadline;

        // ОБЯЗАТЕЛЬНО: пустой конструктор для Jackson
        public CreateTaskRequest() {}

        public CreateTaskRequest(Long groupId, Long createdById,
                                 String title, String description, String deadline) {
            this.groupId = groupId;
            this.createdById = createdById;
            this.title = title;
            this.description = description;
            this.deadline = deadline;
        }
    }
}
