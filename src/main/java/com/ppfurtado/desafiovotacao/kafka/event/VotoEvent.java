package com.ppfurtado.desafiovotacao.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotoEvent {
    private Long votoId;
    private Long sessaoId;
    private String associadoCpf;
    private OpcaoVoto opcaoVoto;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataVoto;
}
