package com.ppfurtado.desafiovotacao.api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura padrão para retorno de erros da API")
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp do momento do erro", example = "2026-09-03T10:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", example = "400")
    private int status;

    @Schema(description = "Descrição do status HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mensagem detalhada do erro", example = "Pauta não encontrada para o ID informado")
    private String message;

    @Schema(description = "Caminho do recurso da requisição", example = "/v1/pautas/999")
    private String path;

    @Schema(description = "Lista de erros de validação por campo, se houver")
    private List<ValidationErrorItem> validationErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorItem {
        private String field;
        private String message;
    }
}
