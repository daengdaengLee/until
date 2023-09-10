package hello.until.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;
    @Size(min=8, max = 20, message = "비밀번호는 8자리 이상 20자 이하로 입력해주세요.")
	private String password;
}
	