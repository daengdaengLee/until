package hello.until.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> exceptionHandler(CustomException exception){
        return ResponseEntity.status(exception.getCode().getHttpStatus())
                .body(new ExceptionResponse(exception.getCode()));
    }

}
