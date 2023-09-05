package hello.until.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionAdvice {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionCode> exceptionHandler(CustomException e){
        return new ResponseEntity<>(e.getCode(), e.getCode().getHttpStatus());
    }
}
