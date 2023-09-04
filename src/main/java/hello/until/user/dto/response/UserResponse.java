package hello.until.user.dto.response;

import hello.until.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private Long id;

    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
