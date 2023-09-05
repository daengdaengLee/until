package hello.until.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ExceptionResponse {
    private HttpStatus status;
    private String message;

    public ExceptionResponse(ExceptionCode exceptionCode) {
        this.status = exceptionCode.getHttpStatus();
        this.message = exceptionCode.getMessage();
    }
}