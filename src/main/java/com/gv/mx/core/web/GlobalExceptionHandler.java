package com.gv.mx.core.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- Helpers ----
    private Map<String, Object> baseBody(HttpServletRequest req, HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", req.getRequestURI());
        body.put("method", req.getMethod());
        body.put("traceId", UUID.randomUUID().toString().replace("-", "").substring(0,16));
        return body;
    }

    private ResponseEntity<Object> resp(HttpServletRequest req, HttpStatus status, String message, List<Map<String, String>> errors) {
        Map<String, Object> body = baseBody(req, status, message);
        if (errors != null && !errors.isEmpty()) body.put("errors", errors);
        return ResponseEntity.status(status).body(body);
    }

    // ---- 400 Bad Request ----
    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> badRequest(Exception ex, HttpServletRequest req) {
        return resp(req, HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    // ---- 401/403 ----
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return resp(req, HttpStatus.FORBIDDEN, "No autorizado para esta operación.", List.of());
    }

    // ---- 404 ----
    @ExceptionHandler({ NoHandlerFoundException.class, MissingPathVariableException.class })
    public ResponseEntity<Object> notFound(Exception ex, HttpServletRequest req) {
        return resp(req, HttpStatus.NOT_FOUND, "Recurso no encontrado.", List.of());
    }

    // ---- 405 ----
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> methodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return resp(req, HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido.", List.of());
    }

    // ---- 415 ----
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> unsupportedMedia(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return resp(req, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Media type no soportado.", List.of());
    }

    // ---- 409 Conflict (FK/unique, etc.) ----
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> conflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        return resp(req, HttpStatus.CONFLICT, "Conflicto de integridad de datos.", List.of());
    }

    // ---- 422 Unprocessable Entity (Bean Validation @Valid) ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> unprocessable(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getAllErrors().stream()
                .map(err -> {
                    String field = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
                    return Map.of("field", field, "message", err.getDefaultMessage());
                })
                .collect(Collectors.toList());
        return resp(req, HttpStatus.UNPROCESSABLE_ENTITY, "Datos inválidos", errors);
    }

    // ---- 422 para @Validated en query params/path vars ----
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        List<Map<String, String>> errors = ex.getConstraintViolations().stream()
                .map(v -> Map.of("field", fieldName(v), "message", v.getMessage()))
                .collect(Collectors.toList());
        return resp(req, HttpStatus.UNPROCESSABLE_ENTITY, "Datos inválidos", errors);
    }

    private String fieldName(ConstraintViolation<?> v) {
        String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        int idx = path.lastIndexOf('.');
        return (idx >= 0 && idx < path.length()-1) ? path.substring(idx+1) : path;
    }

    // ---- Fallback 500 ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> internal(Exception ex, HttpServletRequest req) {
        // Log detallado en server; mensaje corto al cliente.
        return resp(req, HttpStatus.INTERNAL_SERVER_ERROR, "Error interno. Contacta al administrador.", List.of());
    }
}
