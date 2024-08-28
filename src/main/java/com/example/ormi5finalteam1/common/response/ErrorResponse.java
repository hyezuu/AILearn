package com.example.ormi5finalteam1.common.response;

import com.example.ormi5finalteam1.common.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public record ErrorResponse(
    int status,
    String message,
    List<ValidationError> errors
) {

    //dto 필드 유효성 검사
    public static ErrorResponse of(BindingResult bindingResult) {
        return new ErrorResponse(400, "Validation Error",
            ValidationError.ofFieldErrors(bindingResult.getFieldErrors()));
    }

    //파라미터 유효성 검사
    public static ErrorResponse of(Set<ConstraintViolation<?>> violations) {
        return new ErrorResponse(400, "Constraint Violation",
            ValidationError.ofConstraintViolations(violations));
    }

    //커스텀 예외 처리
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.getMessage(),
            Collections.emptyList());
    }

    public static ErrorResponse of(HttpStatus httpStatus) {
        return new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(),
            Collections.emptyList());
    }

    public record ValidationError(
        String field,
        Object rejectedValue,
        String reason
    ) {

        private static List<ValidationError> ofFieldErrors(
            List<org.springframework.validation.FieldError> fieldErrors) {
            return fieldErrors.stream()
                .map(error -> new ValidationError(
                    error.getField(),
                    error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                    error.getDefaultMessage()))
                .toList();
        }

        private static List<ValidationError> ofConstraintViolations(
            Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                .map(violation -> new ValidationError(
                    violation.getPropertyPath().toString(),
                    violation.getInvalidValue().toString(),
                    violation.getMessage()))
                .toList();
        }
    }
}