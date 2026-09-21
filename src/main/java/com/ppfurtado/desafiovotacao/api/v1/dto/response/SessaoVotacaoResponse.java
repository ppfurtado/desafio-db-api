package com.ppfurtado.desafiovotacao.api.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
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
@Schema(description = "Dados da sessão de votação")
public class SessaoVotacaoResponse {

    @Schema(description = "Identificador único da sessão", example = "1")
    private Long id;

    @Schema(description = "Identificador da pauta vinculada", example = "1")
    private Long pautaId;

    @Schema(description = "Título da pauta vinculada", example = "Aprovação do orçamento anual para 2027")
    private String pautaTitulo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data e hora de abertura da sessão", example = "2026-09-03T10:00:00")
    private LocalDateTime dataAbertura;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data e hora prevista para encerramento da sessão", example = "2026-09-03T10:01:00")
    private LocalDateTime dataEncerramento;

    @Schema(description = "Duração configurada em minutos", example = "1")
    private Long duracaoMinutos;

    @Schema(description = "Status atual da sessão", example = "ABERTA")
    private StatusSessao status;

    public static SessaoVotacaoResponse fromEntity(SessaoVotacao sessao) {
        return SessaoVotacaoResponse.builder()
                .id(sessao.getId())
                .pautaId(sessao.getPauta() != null ? sessao.getPauta().getId() : null)
                .pautaTitulo(sessao.getPauta() != null ? sessao.getPauta().getTitulo() : null)
                .dataAbertura(sessao.getDataAbertura())
                .dataEncerramento(sessao.getDataEncerramento())
                .duracaoMinutos(sessao.getDuracaoMinutos())
                .status(sessao.getStatus())
                .build();
    }
}
