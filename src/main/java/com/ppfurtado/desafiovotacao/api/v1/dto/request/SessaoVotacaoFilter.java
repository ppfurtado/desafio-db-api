package com.ppfurtado.desafiovotacao.api.v1.dto.request;

import com.ppfurtado.desafiovotacao.domain.entity.StatusSessao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoVotacaoFilter {

    private String pautaTitulo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate pautaDataCriacaoFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate pautaDataCriacaoTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataAberturaFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataAberturaTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataEncerramentoFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataEncerramentoTo;

    private Long duracaoMinutosMin;

    private Long duracaoMinutosMax;

    private StatusSessao status;
}
