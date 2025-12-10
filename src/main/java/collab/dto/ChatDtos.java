package collab.dto;

public class ChatDtos {

    // ========= REQUEST: SEND MESSAGE =========
    public record SendMessageRequest(
            Long groupId,
            Long userId,
            String message
    ) {}

    // ========= RESPONSE: MESSAGE =========
    public record ChatMessageResponse(
            Long id,
            Long groupId,
            Long userId,
            String userName,
            String message,
            String createdAt
    ) {}
}
