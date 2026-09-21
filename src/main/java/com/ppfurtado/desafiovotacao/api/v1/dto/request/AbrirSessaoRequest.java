package com.ppfurtado.desafiovotacao.api.v1.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para abertura de sessão de votação")
public class AbrirSessaoRequest {

    @NotNull(message = "O ID da pauta é obrigatório")
    @Schema(description = "Identificador único da pauta", example = "1")
    private Long pautaId;

    @Positive(message = "A duração deve ser maior que zero")
    @Schema(description = "Duração da sessão em minutos. Se não informado, o padrão é 1 minuto.", example = "5", defaultValue = "1")
    private Long duracaoMinutos;
}
