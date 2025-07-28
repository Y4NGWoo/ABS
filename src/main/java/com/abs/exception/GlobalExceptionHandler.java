// src/main/java/com/abs/exception/GlobalExceptionHandler.java
package com.abs.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        // HTTP 400 Bad Request로 상태 설정하고, body에 예외 메시지 그대로 담아서 반환
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
