package com.ppfurtado.desafiovotacao.api.exception;

import com.ppfurtado.desafiovotacao.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleCpfInvalidoException(CpfInvalidoException ex, HttpServletRequest request) {
        log.warn("CPF inválido: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(AssociadoJaVotouException.class)
    public ResponseEntity<ErrorResponse> handleAssociadoJaVotouException(AssociadoJaVotouException ex, HttpServletRequest request) {
        log.warn("Voto duplicado: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(SessaoEncerradaException.class)
    public ResponseEntity<ErrorResponse> handleSessaoEncerradaException(SessaoEncerradaException ex, HttpServletRequest request) {
        log.warn("Sessão encerrada: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(AssociadoNaoHabilitadoException.class)
    public ResponseEntity<ErrorResponse> handleAssociadoNaoHabilitadoException(AssociadoNaoHabilitadoException ex, HttpServletRequest request) {
        log.warn("Associado não habilitado: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Regra de negócio violada: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Erro de validação nos campos da requisição: {}", ex.getMessage());
        List<ErrorResponse.ValidationErrorItem> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(ErrorResponse.ValidationErrorItem.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Erro de validação nos dados enviados.", request.getRequestURI(), errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Corpo da requisição inválido ou mal formatado: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Requisição mal formatada ou tipo de dado incompatível.", request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Erro interno inesperado na aplicação", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado no servidor.", request.getRequestURI(), null);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path, List<ErrorResponse.ValidationErrorItem> validationErrors) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .validationErrors(validationErrors)
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}
