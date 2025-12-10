package collab.dto;

import java.time.Instant;

public class GroupDto {

    // --- CREATE REQUEST ---
    public static class CreateGroupRequest {
        private String name;
        private String description;
        private Long creatorId;

        public CreateGroupRequest() {}

        public CreateGroupRequest(String name, String description, Long creatorId) {
            this.name = name;
            this.description = description;
            this.creatorId = creatorId;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public Long getCreatorId() { return creatorId; }
    }

    // --- RESPONSE DTO ---
    public static class GroupResponse {

        private Long id;
        private String name;
        private String description;
        private Long createdById;
        private Instant createdAt;

        // ⭐ ДОБАВЛЕНО ДЛЯ РАЗДЕЛЕНИЯ РОЛЕЙ
        private String yourRole;

        public GroupResponse() {}

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public Long getCreatedById() { return createdById; }
        public Instant getCreatedAt() { return createdAt; }
        public String getYourRole() { return yourRole; }

        @Override
        public String toString() {
            return name;
        }
    }
}
