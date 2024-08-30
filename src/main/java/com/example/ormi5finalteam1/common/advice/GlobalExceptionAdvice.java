package com.example.ormi5finalteam1.common.advice;

import com.example.ormi5finalteam1.common.exception.AlanAIClientException;
import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
    ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());

    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
  }

  // 데이터베이스 위반 체크
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException e) {
    ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT, e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
      AuthorizationDeniedException e, WebRequest request) {
    String error = "접근 권한이 없습니다.";
    ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, error);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  // @Valid 유효성 검사시 (dto 필드)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorResponse errorResponse = ErrorResponse.of(e.getBindingResult());
    log.warn("Validation error: ", e.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }

  // @Validate 유효성검사시 (컨트롤러 메서드 파라미터)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    ErrorResponse errorResponse = ErrorResponse.of(e.getConstraintViolations());
    log.warn("Constraint violation: ", e.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage());
    log.warn("Method not allowed: ", e.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MissingServletRequestParameterException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception e) {
    String message =
        (e instanceof HttpMessageNotReadableException)
            ? "Required request body is missing"
            : e.getMessage();
    ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, message);
    log.warn("Bad request: ", e.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Unhandled exception occurred: ", e);
    ErrorResponse errorResponse =
        ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    return ResponseEntity.internalServerError().body(errorResponse);
  }

  /** AlanAIClientException 처리 */
  @ExceptionHandler(AlanAIClientException.class)
  public ResponseEntity<ErrorResponse> handleAlanAIClientException(AlanAIClientException e) {
    log.warn("Alan AI client exception occurred: ", e.getMessage());
    ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }
}
