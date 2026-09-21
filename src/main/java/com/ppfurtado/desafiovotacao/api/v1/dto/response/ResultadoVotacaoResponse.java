package com.ppfurtado.desafiovotacao.api.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppfurtado.desafiovotacao.domain.entity.ResultadoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.StatusSessao;
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
@Schema(description = "Resultado apurado da votação de uma pauta")
public class ResultadoVotacaoResponse {

    @Schema(description = "Identificador da pauta", example = "1")
    private Long pautaId;

    @Schema(description = "Título da pauta", example = "Aprovação do orçamento anual para 2027")
    private String pautaTitulo;

    @Schema(description = "Identificador da sessão", example = "1")
    private Long sessaoId;

    @Schema(description = "Status da sessão de votação", example = "ENCERRADA")
    private StatusSessao statusSessao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de encerramento da sessão", example = "2026-09-03T10:01:00")
    private LocalDateTime dataEncerramento;

    @Schema(description = "Total geral de votos apurados", example = "150")
    private Long totalVotos;

    @Schema(description = "Total de votos 'SIM'", example = "120")
    private Long totalVotosSim;

    @Schema(description = "Total de votos 'NÃO'", example = "30")
    private Long totalVotosNao;

    @Schema(description = "Percentual de votos 'SIM'", example = "80.0%")
    private String percentualSim;

    @Schema(description = "Percentual de votos 'NÃO'", example = "20.0%")
    private String percentualNao;

    @Schema(description = "Resultado final da votação", example = "APROVADA")
    private ResultadoVotacao resultado;
}
