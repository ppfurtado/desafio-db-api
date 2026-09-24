package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.VotoCreateRequest;
import com.ppfurtado.desafiovotacao.client.CpfValidatorUtil;
import com.ppfurtado.desafiovotacao.domain.exception.AssociadoJaVotouException;
import com.ppfurtado.desafiovotacao.kafka.event.VotoEvent;
import com.ppfurtado.desafiovotacao.kafka.producer.VotoProducer;
import com.ppfurtado.desafiovotacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoProducer votoProducer;
    private final VotoRepository votoRepository;

    public void registrarVoto(Long sessaoId, VotoCreateRequest request) {

        String cpfLimpo = CpfValidatorUtil.sanitize(request.getAssociadoCpf());
        if (cpfLimpo.isEmpty()) {
            cpfLimpo = request.getAssociadoCpf().trim();
        }

        if (votoRepository.existsBySessaoIdAndAssociadoCpf(sessaoId, cpfLimpo)) {
            log.warn("Associado {} já votou na sessão ID {}.", cpfLimpo, sessaoId);
            throw new AssociadoJaVotouException("Associado com identificador informado já votou nesta pauta.");
        }

        VotoEvent event = VotoEvent.builder()
                .sessaoId(sessaoId)
                .associadoCpf(cpfLimpo)
                .opcaoVoto(request.getOpcaoVoto())
                .dataVoto(LocalDateTime.now())
                .build();

        votoProducer.publishVoto(event);
    }
}
