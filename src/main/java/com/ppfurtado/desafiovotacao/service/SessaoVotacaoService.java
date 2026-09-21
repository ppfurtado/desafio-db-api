package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.AbrirSessaoRequest;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.domain.exception.BusinessException;
import com.ppfurtado.desafiovotacao.domain.exception.ResourceNotFoundException;
import com.ppfurtado.desafiovotacao.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.SessaoVotacaoFilter;
import com.ppfurtado.desafiovotacao.repository.SessaoVotacaoSpecification;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaService pautaService;

    @Value("${app.session.default-duration-minutes:1}")
    private Long defaultDurationMinutes;

    @Transactional
    public SessaoVotacao abrirSessao(AbrirSessaoRequest request) {
        Pauta pauta = pautaService.buscarPorId(request.getPautaId());

        List<SessaoVotacao> sessoesExistentes = sessaoVotacaoRepository.findByPautaId(pauta.getId());
        if (sessoesExistentes != null && !sessoesExistentes.isEmpty()) {
            boolean existeAberta = sessoesExistentes.stream().anyMatch(SessaoVotacao::isAberta);
            if (existeAberta) {
                log.warn("Tentativa de abrir sessão duplicada (já aberta) para a pauta ID: {}", pauta.getId());
                throw new BusinessException("Já existe uma sessão de votação aberta para esta pauta.");
            }
        }

        long duracao = (request.getDuracaoMinutos() != null && request.getDuracaoMinutos() > 0)
                ? request.getDuracaoMinutos()
                : defaultDurationMinutes;

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime encerramento = agora.plusMinutes(duracao);

        SessaoVotacao sessao = SessaoVotacao.builder()
                .pauta(pauta)
                .dataAbertura(agora)
                .dataEncerramento(encerramento)
                .duracaoMinutos(duracao)
                .build();

        SessaoVotacao salva = sessaoVotacaoRepository.save(sessao);
        log.info("Sessão ID {} aberta para a pauta ID {} com duração de {} minuto(s), encerramento em {}",
                salva.getId(), pauta.getId(), duracao, encerramento);

        return salva;
    }

    @Transactional(readOnly = true)
    public SessaoVotacao buscarPorId(Long id) {
        return sessaoVotacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de votação não encontrada para o ID: " + id));
    }

    @Cacheable(value = "sessoesPorPauta", key = "#pautaId")
    @Transactional(readOnly = true)
    public SessaoVotacao buscarPorPautaId(Long pautaId) {
        List<SessaoVotacao> sessoes = sessaoVotacaoRepository.findByPautaId(pautaId);
        if (sessoes == null || sessoes.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma sessão de votação encontrada para a pauta ID: " + pautaId);
        }
        return sessoes.stream()
                .max(java.util.Comparator.comparing(SessaoVotacao::getDataAbertura))
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma sessão de votação encontrada para a pauta ID: " + pautaId));
    }

    @Transactional(readOnly = true)
    public Page<SessaoVotacao> listarTodas(SessaoVotacaoFilter filter, Pageable pageable) {
        Specification<SessaoVotacao> spec = SessaoVotacaoSpecification.of(filter);
        return sessaoVotacaoRepository.findAll(spec, pageable);
    }
}
