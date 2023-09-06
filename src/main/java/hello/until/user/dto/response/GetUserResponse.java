package hello.until.user.dto.response;

import java.time.LocalDateTime;

import hello.until.user.constant.Role;
import hello.until.user.entity.User;
import lombok.Getter;

@Getter
public class GetUserResponse {
    private final Long id;
    private final String email;
    private final Role role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public GetUserResponse(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
