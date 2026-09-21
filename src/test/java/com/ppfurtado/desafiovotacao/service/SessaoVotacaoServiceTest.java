package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.AbrirSessaoRequest;
import com.ppfurtado.desafiovotacao.api.v1.dto.request.SessaoVotacaoFilter;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.domain.exception.BusinessException;
import com.ppfurtado.desafiovotacao.domain.exception.ResourceNotFoundException;
import com.ppfurtado.desafiovotacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private SessaoVotacaoService sessaoVotacaoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sessaoVotacaoService, "defaultDurationMinutes", 1L);
    }

    @Test
    void abrirSessao_shouldPersistNewSession() {
        Pauta pauta = Pauta.builder().id(10L).titulo("Pauta Teste").descricao("desc").build();
        AbrirSessaoRequest request = new AbrirSessaoRequest(10L, 5L);

        when(pautaService.buscarPorId(10L)).thenReturn(pauta);
        when(sessaoVotacaoRepository.findByPautaId(10L)).thenReturn(List.of());

        SessaoVotacao salvo = SessaoVotacao.builder()
                .id(99L)
                .pauta(pauta)
                .dataAbertura(LocalDateTime.now())
                .dataEncerramento(LocalDateTime.now().plusMinutes(5))
                .duracaoMinutos(5L)
                .build();
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(salvo);

        SessaoVotacao result = sessaoVotacaoService.abrirSessao(request);

        ArgumentCaptor<SessaoVotacao> captor = ArgumentCaptor.forClass(SessaoVotacao.class);
        verify(sessaoVotacaoRepository).save(captor.capture());

        SessaoVotacao entity = captor.getValue();
        assertEquals(10L, entity.getPauta().getId());
        assertEquals(5L, entity.getDuracaoMinutos());
        assertNotNull(entity.getDataAbertura());
        assertEquals(99L, result.getId());
    }

    @Test
    void abrirSessao_whenSessionAlreadyExists_shouldThrowBusinessException() {
        Pauta pauta = Pauta.builder().id(11L).titulo("Pauta duplicada").build();
        when(pautaService.buscarPorId(11L)).thenReturn(pauta);
        // create an existing open session (encerramento no futuro)
        SessaoVotacao existente = SessaoVotacao.builder()
                .id(55L)
                .pauta(pauta)
                .dataAbertura(LocalDateTime.now().minusMinutes(1))
                .dataEncerramento(LocalDateTime.now().plusMinutes(10))
                .duracaoMinutos(11L)
                .build();
        when(sessaoVotacaoRepository.findByPautaId(11L)).thenReturn(List.of(existente));

        BusinessException ex = assertThrows(BusinessException.class, () -> sessaoVotacaoService.abrirSessao(new AbrirSessaoRequest(11L, 3L)));
        assertTrue(ex.getMessage().toLowerCase().contains("aberta") || ex.getMessage().toLowerCase().contains("já existe"));

        // verify interactions: service looked up pauta and checked repository, but did not save
        verify(pautaService, times(1)).buscarPorId(11L);
        verify(sessaoVotacaoRepository, times(1)).findByPautaId(11L);
        verify(sessaoVotacaoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_shouldReturnSession() {
        Pauta pauta = Pauta.builder().id(12L).titulo("Pauta").build();
        SessaoVotacao sessao = SessaoVotacao.builder().id(123L).pauta(pauta).dataAbertura(LocalDateTime.now()).dataEncerramento(LocalDateTime.now().plusMinutes(5)).duracaoMinutos(5L).build();
        when(sessaoVotacaoRepository.findById(123L)).thenReturn(Optional.of(sessao));

        SessaoVotacao result = sessaoVotacaoService.buscarPorId(123L);

        assertEquals(123L, result.getId());
    }

    @Test
    void buscarPorId_whenNotFound_shouldThrowResourceNotFoundException() {
        when(sessaoVotacaoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sessaoVotacaoService.buscarPorId(999L));
    }

    @Test
    void buscarPorPautaId_shouldReturnSession() {
        Pauta pauta = Pauta.builder().id(20L).titulo("Pauta").build();
        SessaoVotacao sessao = SessaoVotacao.builder().id(200L).pauta(pauta).dataAbertura(LocalDateTime.now()).dataEncerramento(LocalDateTime.now().plusMinutes(5)).duracaoMinutos(5L).build();
        when(sessaoVotacaoRepository.findByPautaId(20L)).thenReturn(List.of(sessao));

        SessaoVotacao result = sessaoVotacaoService.buscarPorPautaId(20L);

        assertEquals(200L, result.getId());
    }

    @Test
    void buscarPorPautaId_whenNotFound_shouldThrowResourceNotFoundException() {
        when(sessaoVotacaoRepository.findByPautaId(404L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> sessaoVotacaoService.buscarPorPautaId(404L));
    }

    @Test
    void listarTodas_shouldDelegateToRepository() {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<SessaoVotacao> page = new PageImpl<>(List.of(SessaoVotacao.builder().id(1L).build()), pageable, 1);
        when(sessaoVotacaoRepository.findAll((org.springframework.data.jpa.domain.Specification<SessaoVotacao>) any(), eq(pageable))).thenReturn(page);

        Page<SessaoVotacao> result = sessaoVotacaoService.listarTodas(SessaoVotacaoFilter.builder().build(), pageable);

        assertEquals(1, result.getTotalElements());
        verify(sessaoVotacaoRepository).findAll((org.springframework.data.jpa.domain.Specification<SessaoVotacao>) any(), eq(pageable));
    }
}
