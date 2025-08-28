// src/main/java/com/abs/exception/GlobalExceptionHandler.java
package com.abs.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleRuntime(IllegalArgumentException ex) {
        // HTTP 400 Bad Request로 상태 설정하고, body에 예외 메시지 그대로 담아서 반환

        if(ex.getMessage().startsWith("LOGIN_ERR")){
            return ResponseEntity
                    .badRequest()
                    .body("아이디 혹은 비밀번호가 틀렸습니다.");
        }

        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
