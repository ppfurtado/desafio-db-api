package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.PautaCreateRequest;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.exception.ResourceNotFoundException;
import com.ppfurtado.desafiovotacao.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    void cadastrar_trimsTitleAndPersists() {
        PautaCreateRequest req = new PautaCreateRequest();
        req.setTitulo("  Minha Pauta  ");
        req.setDescricao("  Desc  ");

        Pauta saved = Pauta.builder().id(5L).titulo("Minha Pauta").descricao("Desc").build();
        when(pautaRepository.save(org.mockito.ArgumentMatchers.any(Pauta.class))).thenReturn(saved);

        Pauta result = pautaService.cadastrar(req);

        assertEquals(5L, result.getId());
        assertEquals("Minha Pauta", result.getTitulo());
        assertEquals("Desc", result.getDescricao());
    }

    @Test
    void buscarPorId_notFound_throws() {
        when(pautaRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> pautaService.buscarPorId(999L));
    }

    @Test
    void listar_delegatesToRepository() {
        PageRequest pr = PageRequest.of(0, 10);
        Page<Pauta> page = new PageImpl<>(List.of(Pauta.builder().id(1L).titulo("T").build()));
        when(pautaRepository.findAll(pr)).thenReturn(page);

        Page<Pauta> result = pautaService.listar(pr);
        assertEquals(1, result.getTotalElements());
    }
}
