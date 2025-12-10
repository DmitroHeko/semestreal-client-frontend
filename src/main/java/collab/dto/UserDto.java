package collab.dto;

public class UserDto {

    public static class UserResponse {
        private Long id;
        private String name;
        private String email;

        public UserResponse() {}

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return name;
        }
    }
}
