package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.VotoCreateRequest;
import com.ppfurtado.desafiovotacao.client.CpfValidationFacade;
import com.ppfurtado.desafiovotacao.client.CpfValidatorUtil;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.Voto;
import com.ppfurtado.desafiovotacao.domain.exception.AssociadoJaVotouException;
import com.ppfurtado.desafiovotacao.domain.exception.AssociadoNaoHabilitadoException;
import com.ppfurtado.desafiovotacao.domain.exception.SessaoEncerradaException;
import com.ppfurtado.desafiovotacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService {

    private final SessaoVotacaoService sessaoVotacaoService; // Deve utilizar cache local/Redis internamente
    private final VotoRepository votoRepository;

    @Transactional
    public void registrarVoto(Long sessaoId, VotoCreateRequest request) {

        // 1. Sanitize simples em memória
        String cpfLimpo = CpfValidatorUtil.sanitize(request.getAssociadoCpf());
        if (cpfLimpo.isEmpty()) {
            cpfLimpo = request.getAssociadoCpf().trim();
        }

        // 2. Valida se o associado já votou nesta sessão
        if (votoRepository.existsBySessaoIdAndAssociadoCpf(sessaoId, cpfLimpo)) {
            log.warn("Associado {} já votou na sessão ID {}.", cpfLimpo, sessaoId);
            throw new AssociadoJaVotouException("Associado com identificador informado já votou nesta pauta.");
        }

        // 3. Busca a sessão e valida se está aberta
        SessaoVotacao sessao = sessaoVotacaoService.buscarPorId(sessaoId);
        if (!sessao.isAberta()) {
            log.warn("Sessão {} encerrada ao tentar registrar voto do associado {}", sessaoId, cpfLimpo);
            throw new SessaoEncerradaException("Sessão de votação está encerrada.");
        }

        // 4. Persiste o voto diretamente no banco
        Voto voto = Voto.builder()
                .sessao(sessao)
                .associadoCpf(cpfLimpo)
                .opcaoVoto(request.getOpcaoVoto())
                .dataVoto(LocalDateTime.now())
                .build();

        Voto salvo = votoRepository.save(voto);
        log.info("Voto persistido ID {} (sessao {})", salvo.getId(), sessaoId);
    }
}
