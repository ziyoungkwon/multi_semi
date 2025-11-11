package com.multi.multi_semi.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // 500
    public ResponseEntity<ApiExceptionDto> exceptionHandler(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(new ApiExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiExceptionDto> invalidPasswordException(InvalidPasswordException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(new ApiExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(DuplicateUserEmailException.class)
    public ResponseEntity<ApiExceptionDto> duplicateUsernameException(DuplicateUserEmailException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiExceptionDto(HttpStatus.CONFLICT, e.getMessage()));
    }

    @ExceptionHandler(DuplicateUserIdException.class)
    public ResponseEntity<ApiExceptionDto> duplicateUserIdException(DuplicateUserIdException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiExceptionDto(HttpStatus.CONFLICT, e.getMessage()));
    }

    @ExceptionHandler(MemberRegistrationException.class)
    public ResponseEntity<ApiExceptionDto> memberRegistrationException(MemberRegistrationException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(new ApiExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiExceptionDto> badCredentialsException(BadCredentialsException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(new ApiExceptionDto(HttpStatus.UNAUTHORIZED, e.getMessage()));
    }


    @ExceptionHandler(RefreshTokenException.class) //401
    public ResponseEntity<ApiExceptionDto> exceptionHandler(RefreshTokenException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiExceptionDto(HttpStatus.UNAUTHORIZED, e.getMessage()));
    }

}