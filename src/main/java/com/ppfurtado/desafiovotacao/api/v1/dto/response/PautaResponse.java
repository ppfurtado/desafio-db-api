package com.ppfurtado.desafiovotacao.api.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados da pauta")
public class PautaResponse {

    @Schema(description = "Identificador único da pauta", example = "1")
    private Long id;

    @Schema(description = "Título da pauta", example = "Aprovação do orçamento anual para 2027")
    private String titulo;

    @Schema(description = "Descrição detalhada da pauta", example = "Discussão e votação sobre os investimentos previstos para o próximo exercício.")
    private String descricao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data e hora de criação da pauta", example = "2026-09-03T10:00:00")
    private LocalDateTime dataCriacao;

    public static PautaResponse fromEntity(Pauta pauta) {
        return PautaResponse.builder()
                .id(pauta.getId())
                .titulo(pauta.getTitulo())
                .descricao(pauta.getDescricao())
                .dataCriacao(pauta.getDataCriacao())
                .build();
    }
}
