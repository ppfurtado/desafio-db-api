package com.ppfurtado.desafiovotacao.api.v1.dto.request;

import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para envio de voto de um associado")
public class VotoCreateRequest {

    @NotBlank(message = "O identificador/CPF do associado é obrigatório")
    @Schema(description = "CPF ou Identificador único do associado", example = "58498501061")
    private String associadoCpf;

    @NotNull(message = "A opção de voto (SIM ou NAO) é obrigatória")
    @Schema(description = "Opção de voto: SIM ou NAO", example = "SIM")
    private OpcaoVoto opcaoVoto;
}
