package collab.dto;

public class ResourceDto {

    // ================= RESPONSE DTO =================
    public static class ResourceResponse {
        public Long id;
        public Long groupId;
        public String title;
        public String type;
        public String pathOrUrl;
        public Long uploadedById;
        public String uploadedAt;

        public ResourceResponse() {}

        public Long getId() { return id; }
        public Long getGroupId() { return groupId; }
        public Long getUploadedById() { return uploadedById; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getPathOrUrl() { return pathOrUrl; }
        public String getUploadedAt() { return uploadedAt; }
    }

    // ================= REQUEST DTO =================
    public static class CreateResourceRequest {

        public Long id;
        public Long groupId;
        public String title;
        public String type;
        public String pathOrUrl;
        public Long uploadedById;
        public String uploadedAt;

        // ОБЯЗАТЕЛЬНЫЙ пустой конструктор для Jackson
        public CreateResourceRequest() {}

        public CreateResourceRequest(Long groupId, Long uploadedById,
                                     String title, String type, String pathOrUrl) {

            this.groupId = groupId;
            this.uploadedById = uploadedById;
            this.title = title;
            this.type = type;
            this.pathOrUrl = pathOrUrl;
        }
    }
}

