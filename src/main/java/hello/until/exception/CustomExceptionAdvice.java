package hello.until.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionAdvice {
    @ExceptionHandler
    public ResponseEntity<ExceptionCode> handleCustomException(CustomException e) {
        return new ResponseEntity<>(e.getCode(), e.getCode().getHttpStatus());
    }
}
