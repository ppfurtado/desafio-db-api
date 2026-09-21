package com.ppfurtado.desafiovotacao.api.v1.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de uma nova pauta")
public class PautaCreateRequest {

    @NotBlank(message = "O título da pauta é obrigatório")
    @Size(min = 3, max = 200, message = "O título da pauta deve ter entre 3 e 200 caracteres")
    @Schema(description = "Título da pauta", example = "Aprovação do orçamento anual para 2027")
    private String titulo;

    @Schema(description = "Descrição detalhada da pauta", example = "Discussão e votação sobre os investimentos previstos para o próximo exercício.")
    private String descricao;
}
