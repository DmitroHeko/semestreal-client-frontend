package collab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class AuthDto {

    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;

        public RegisterRequest() {}
        public RegisterRequest(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }

        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }

        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;

        public UserResponse() {}

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }

        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
    }
}
