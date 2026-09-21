package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.PautaCreateRequest;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.exception.ResourceNotFoundException;
import com.ppfurtado.desafiovotacao.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;

    @Transactional
    public Pauta cadastrar(PautaCreateRequest request) {
        log.info("Cadastrando nova pauta: {}", request.getTitulo());

        Pauta pauta = Pauta.builder()
                .titulo(request.getTitulo().trim())
                .descricao(request.getDescricao() != null ? request.getDescricao().trim() : null)
                .build();

        Pauta salva = pautaRepository.save(pauta);
        log.info("Pauta cadastrada com sucesso com ID: {}", salva.getId());
        return salva;
    }

    @Transactional(readOnly = true)
    public Pauta buscarPorId(Long id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada para o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Pauta> listar(Pageable pageable) {
        return pautaRepository.findAll(pageable);
    }
}
