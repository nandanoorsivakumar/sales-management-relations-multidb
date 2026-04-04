package com.example.salesrelations.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;
import com.example.salesrelations.mongo.service.AuditLogService;
import com.example.salesrelations.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AuditLogService auditLogService;

    public GlobalExceptionHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        String username = getCurrentUsername();

        String message = errors.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        AuditAction action = resolveAuditAction(request);

        if (action != null) {
            auditLogService.saveLog(
                    action,
                    username,
                    resolveEntityName(request),
                    "N/A",
                    AuditStatus.FAILED,
                    message
            );
        }

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String username = getCurrentUsername();
        AuditAction action = resolveAuditAction(request);

        if (action != null) {
            auditLogService.saveLog(
                    action,
                    username,
                    resolveEntityName(request),
                    "N/A",
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
        }

        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong: " + ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        return "SYSTEM";
    }

    private AuditAction resolveAuditAction(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if ("/employees".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return AuditAction.CREATE_EMPLOYEE;
        }
        if (uri.startsWith("/employees/") && "PUT".equalsIgnoreCase(method)) {
            return AuditAction.UPDATE_EMPLOYEE;
        }
        if (uri.startsWith("/employees/") && "DELETE".equalsIgnoreCase(method)) {
            return AuditAction.DELETE_EMPLOYEE;
        }

        if ("/customers".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return AuditAction.CREATE_CUSTOMER;
        }
        if (uri.startsWith("/customers/") && "PUT".equalsIgnoreCase(method)) {
            return AuditAction.UPDATE_CUSTOMER;
        }
        if (uri.startsWith("/customers/") && "DELETE".equalsIgnoreCase(method)) {
            return AuditAction.DELETE_CUSTOMER;
        }

        if ("/orders".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return AuditAction.CREATE_ORDER;
        }
        if (uri.startsWith("/orders/") && "PUT".equalsIgnoreCase(method)) {
            return AuditAction.UPDATE_ORDER;
        }
        if (uri.startsWith("/orders/") && "DELETE".equalsIgnoreCase(method)) {
            return AuditAction.DELETE_ORDER;
        }

        if ("/auth/login".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return AuditAction.LOGIN;
        }

        return null;
    }

    private String resolveEntityName(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/employees")) return "Employee";
        if (uri.startsWith("/customers")) return "Customer";
        if (uri.startsWith("/orders")) return "Order";
        if (uri.startsWith("/auth/login")) return "Login";

        return "Unknown";
    }
}



















//package com.example.salesrelations.exception;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import com.example.salesrelations.response.ApiResponse;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
//        ApiResponse<Object> response = new ApiResponse<>(
//                HttpStatus.NOT_FOUND.value(),
//                ex.getMessage(),
//                null
//        );
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
//        ApiResponse<Object> response = new ApiResponse<>(
//                HttpStatus.BAD_REQUEST.value(),
//                ex.getMessage(),
//                null
//        );
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(DuplicateResourceException.class)
//    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
//        ApiResponse<Object> response = new ApiResponse<>(
//                HttpStatus.CONFLICT.value(),
//                ex.getMessage(),
//                null
//        );
//        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        ApiResponse<Map<String, String>> response = new ApiResponse<>(
//                HttpStatus.BAD_REQUEST.value(),
//                "Validation failed",
//                errors
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
//        ApiResponse<Object> response = new ApiResponse<>(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Something went wrong: " + ex.getMessage(),
//                null
//        );
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}*/
