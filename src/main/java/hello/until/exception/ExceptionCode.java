package hello.until.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExceptionCode {
	NO_USER_TO_GET(HttpStatus.BAD_REQUEST, "해당 유저 정보가 없습니다."),
    NO_USER_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 유저가 없습니다."),
    NO_ITEM_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 상품이 없습니다."),
    DUPLICATE_EMAIL_USER_TO_CREATE(HttpStatus.CONFLICT, "이미 가입 된 회원 메일입니다.");


    @JsonIgnore
    private final HttpStatus httpStatus;
    private final String message;
}
