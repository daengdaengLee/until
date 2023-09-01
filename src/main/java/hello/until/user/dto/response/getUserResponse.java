package hello.until.user.dto.response;

import java.time.LocalDateTime;

import hello.until.user.entity.User;
import lombok.Getter;

@Getter
public class getUserResponse {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public getUserResponse(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
