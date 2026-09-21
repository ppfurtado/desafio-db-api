package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.domain.entity.ResultadoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.repository.VotoRepository;
import com.ppfurtado.desafiovotacao.repository.projection.VotoContabilizadoProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultadoVotacaoService {

    private final SessaoVotacaoService sessaoVotacaoService;
    private final VotoRepository votoRepository;

    @Transactional(readOnly = true)
    public ResultadoVotacaoResponse contabilizarResultadoPorPauta(Long pautaId) {
        SessaoVotacao sessao = sessaoVotacaoService.buscarPorPautaId(pautaId);
        return contabilizar(sessao);
    }

    @Transactional(readOnly = true)
    public ResultadoVotacaoResponse contabilizarResultadoPorSessao(Long sessaoId) {
        SessaoVotacao sessao = sessaoVotacaoService.buscarPorId(sessaoId);
        return contabilizar(sessao);
    }

    private ResultadoVotacaoResponse contabilizar(SessaoVotacao sessao) {
        List<VotoContabilizadoProjection> contagens = votoRepository.countVotosBySessaoIdGrouped(sessao.getId());

        long totalSim = 0L;
        long totalNao = 0L;

        for (VotoContabilizadoProjection item : contagens) {
            if (OpcaoVoto.SIM.equals(item.getOpcao())) {
                totalSim = item.getTotal();
            } else if (OpcaoVoto.NAO.equals(item.getOpcao())) {
                totalNao = item.getTotal();
            }
        }

        long totalGeral = totalSim + totalNao;
        String percSim = totalGeral > 0 ? String.format(Locale.US, "%.2f%%", (totalSim * 100.0) / totalGeral) : "0.00%";
        String percNao = totalGeral > 0 ? String.format(Locale.US, "%.2f%%", (totalNao * 100.0) / totalGeral) : "0.00%";

        ResultadoVotacao resultado;
        if (totalGeral == 0) {
            resultado = ResultadoVotacao.SEM_VOTOS;
        } else if (totalSim > totalNao) {
            resultado = ResultadoVotacao.APROVADA;
        } else if (totalNao > totalSim) {
            resultado = ResultadoVotacao.REJEITADA;
        } else {
            resultado = ResultadoVotacao.EMPATE;
        }

        log.info("Resultado apurado da sessão {}: Total={}, Sim={}, Não={}, Resultado={}",
                sessao.getId(), totalGeral, totalSim, totalNao, resultado);

        return ResultadoVotacaoResponse.builder()
                .pautaId(sessao.getPauta().getId())
                .pautaTitulo(sessao.getPauta().getTitulo())
                .sessaoId(sessao.getId())
                .statusSessao(sessao.getStatus())
                .dataEncerramento(sessao.getDataEncerramento())
                .totalVotos(totalGeral)
                .totalVotosSim(totalSim)
                .totalVotosNao(totalNao)
                .percentualSim(percSim)
                .percentualNao(percNao)
                .resultado(resultado)
                .build();
    }
}
