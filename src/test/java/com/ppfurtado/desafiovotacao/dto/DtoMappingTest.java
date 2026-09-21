package com.ppfurtado.desafiovotacao.dto;

import com.ppfurtado.desafiovotacao.api.v1.dto.response.PautaResponse;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.SessaoVotacaoResponse;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.VotoResponse;
import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.Voto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoMappingTest {

    @Test
    void pautaResponse_fromEntity_mapsFields() {
        Pauta pauta = Pauta.builder().id(5L).titulo("T").descricao("D").dataCriacao(LocalDateTime.now()).build();
        PautaResponse resp = PautaResponse.fromEntity(pauta);
        assertEquals(5L, resp.getId());
        assertEquals("T", resp.getTitulo());
        assertEquals("D", resp.getDescricao());
    }

    @Test
    void sessaoResponse_fromEntity_mapsFields() {
        Pauta pauta = Pauta.builder().id(7L).titulo("PT").build();
        SessaoVotacao sessao = SessaoVotacao.builder().id(8L).pauta(pauta).dataAbertura(LocalDateTime.now()).dataEncerramento(LocalDateTime.now().plusMinutes(5)).duracaoMinutos(5L).build();
        SessaoVotacaoResponse resp = SessaoVotacaoResponse.fromEntity(sessao);
        assertEquals(8L, resp.getId());
        assertEquals(7L, resp.getPautaId());
        assertEquals("PT", resp.getPautaTitulo());
    }

    @Test
    void votoResponse_fromEntity_mapsFields() {
        Pauta pauta = Pauta.builder().id(9L).titulo("P").build();
        SessaoVotacao sessao = SessaoVotacao.builder().id(10L).pauta(pauta).build();
        Voto voto = Voto.builder().id(11L).sessao(sessao).associadoCpf("123").opcaoVoto(OpcaoVoto.SIM).dataVoto(LocalDateTime.now()).build();
        VotoResponse resp = VotoResponse.fromEntity(voto);
        assertEquals(11L, resp.getId());
        assertEquals(10L, resp.getSessaoId());
        assertEquals(9L, resp.getPautaId());
        assertEquals("123", resp.getAssociadoCpf());
        assertEquals(OpcaoVoto.SIM, resp.getOpcaoVoto());
        assertEquals("Voto registrado com sucesso!", resp.getMensagem());
    }
}
