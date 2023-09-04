package hello.until.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    NO_ITEM_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 상품이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
