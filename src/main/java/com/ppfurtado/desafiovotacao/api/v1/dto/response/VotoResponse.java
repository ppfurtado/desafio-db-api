package com.ppfurtado.desafiovotacao.api.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.domain.entity.Voto;
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
@Schema(description = "Confirmação de registro do voto")
public class VotoResponse {

    @Schema(description = "Identificador único do voto registrado", example = "1")
    private Long id;

    @Schema(description = "Identificador da sessão", example = "1")
    private Long sessaoId;

    @Schema(description = "Identificador da pauta", example = "1")
    private Long pautaId;

    @Schema(description = "CPF do associado", example = "58498501061")
    private String associadoCpf;

    @Schema(description = "Opção de voto registrada", example = "SIM")
    private OpcaoVoto opcaoVoto;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data e hora do voto", example = "2026-09-03T10:00:30")
    private LocalDateTime dataVoto;

    @Schema(description = "Mensagem descritiva", example = "Voto registrado com sucesso!")
    private String mensagem;

    public static VotoResponse fromEntity(Voto voto) {
        return VotoResponse.builder()
                .id(voto.getId())
                .sessaoId(voto.getSessao() != null ? voto.getSessao().getId() : null)
                .pautaId(voto.getSessao() != null && voto.getSessao().getPauta() != null ? voto.getSessao().getPauta().getId() : null)
                .associadoCpf(voto.getAssociadoCpf())
                .opcaoVoto(voto.getOpcaoVoto())
                .dataVoto(voto.getDataVoto())
                .mensagem("Voto registrado com sucesso!")
                .build();
    }
}
