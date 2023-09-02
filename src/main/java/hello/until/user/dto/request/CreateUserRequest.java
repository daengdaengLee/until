package hello.until.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUserRequest {

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;
    @Size(min=8, max = 20, message = "비밀번호는 8자리 이상 20자 이하로 입력해주세요.")
    private String password;

}
