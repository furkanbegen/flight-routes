package com.furkanbegen.routes.exception;

import com.furkanbegen.routes.dto.ErrorResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionHandler {

  private GeneralExceptionHandler() {}

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  static ResponseEntity<ErrorResponse> resolveMethodArgumentNotValidException(
      final MethodArgumentNotValidException ex) {
    final List<String> messages = new ArrayList<>();
    (ex.getBindingResult())
        .getAllErrors()
        .forEach(error -> messages.add(error.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .messages(messages)
                .build());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  static ResponseEntity<ErrorResponse> resolveDataIntegrityViolationException(
      final DataIntegrityViolationException ex) {

    String errorMessage = Objects.requireNonNullElse(ex.getRootCause(), ex).getMessage();

    String message =
        errorMessage == null
            ? "Duplicate key violation: Unknown cause"
            : "Duplicate key violation: " + errorMessage;

    var errorResponse =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .error("Data Integrity Violation")
            .status(HttpStatus.BAD_REQUEST.value())
            .messages(Collections.singletonList(message))
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  static ResponseEntity<ErrorResponse> resolveResourceNotFoundException(
      final ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .messages(List.of(ex.getMessage()))
                .build());
  }
}
