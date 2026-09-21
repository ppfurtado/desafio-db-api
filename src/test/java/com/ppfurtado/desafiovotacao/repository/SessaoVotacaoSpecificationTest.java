package com.ppfurtado.desafiovotacao.repository;

import com.ppfurtado.desafiovotacao.domain.entity.StatusSessao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;

import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoSpecificationTest {

    @Mock
    Root root;

    @Mock
    CriteriaQuery query;

    @Mock
    CriteriaBuilder cb;

    @Mock
    Join join;

    @Mock
    Path path;

    @Mock
    Expression lowered;

    @Mock
    Predicate predicate;

    @Test
    void hasPautaTitulo_null_returnsNullPredicate() {
        var spec = SessaoVotacaoSpecification.hasPautaTitulo(null);
        assertNotNull(spec);
        Predicate p = spec.toPredicate(root, query, cb);
        assertNull(p);
    }

    @Test
    void hasPautaTitulo_nonNull_usesJoinAndLike() {
        String titulo = "Teste";
        when(root.join("pauta")).thenReturn(join);
        when(join.get("titulo")).thenReturn(path);
        when(cb.lower(path)).thenReturn(lowered);
        when(cb.like(lowered, "%" + titulo.toLowerCase() + "%")).thenReturn(predicate);

        var spec = SessaoVotacaoSpecification.hasPautaTitulo(titulo);
        Predicate p = spec.toPredicate(root, query, cb);

        assertNotNull(p);
        assertEquals(predicate, p);
        verify(root).join("pauta");
        verify(cb).like(lowered, "%" + titulo.toLowerCase() + "%");
    }

    @Test
    void hasDataAberturaFrom_null_returnsNull() {
        var spec = SessaoVotacaoSpecification.hasDataAberturaFrom(null);
        assertNotNull(spec);
        Predicate p = spec.toPredicate(root, query, cb);
        assertNull(p);
    }

    @Test
    void hasDataAberturaFrom_nonNull_callsGreaterThanOrEqual() {
        LocalDateTime from = LocalDateTime.now();
        when(root.get("dataAbertura")).thenReturn(path);
        when(cb.greaterThanOrEqualTo(path, from)).thenReturn(predicate);

        var spec = SessaoVotacaoSpecification.hasDataAberturaFrom(from);
        Predicate p = spec.toPredicate(root, query, cb);

        assertNotNull(p);
        assertEquals(predicate, p);
        verify(cb).greaterThanOrEqualTo(path, from);
    }

    @Test
    void hasPautaDataCriacaoFrom_null_returnsNull() {
        var spec = SessaoVotacaoSpecification.hasPautaDataCriacaoFrom(null);
        assertNotNull(spec);
        Predicate p = spec.toPredicate(root, query, cb);
        assertNull(p);
    }

    @Test
    void hasPautaDataCriacaoFrom_nonNull_usesJoinAndGreaterThanOrEqual() {
        LocalDate from = LocalDate.now();
        when(root.join("pauta")).thenReturn(join);
        when(join.get("dataCriacao")).thenReturn(path);
        when(cb.greaterThanOrEqualTo(any(), any(LocalDateTime.class))).thenReturn(predicate);

        var spec = SessaoVotacaoSpecification.hasPautaDataCriacaoFrom(from);
        Predicate p = spec.toPredicate(root, query, cb);

        assertNotNull(p);
        assertEquals(predicate, p);
        verify(root).join("pauta");
        verify(cb).greaterThanOrEqualTo(any(), any(LocalDateTime.class));
    }

    @Test
    void hasPautaDataCriacaoTo_null_returnsNull() {
        var spec = SessaoVotacaoSpecification.hasPautaDataCriacaoTo(null);
        assertNotNull(spec);
        Predicate p = spec.toPredicate(root, query, cb);
        assertNull(p);
    }

    @Test
    void hasPautaDataCriacaoTo_nonNull_usesJoinAndLessThanOrEqual() {
        LocalDate to = LocalDate.now();
        when(root.join("pauta")).thenReturn(join);
        when(join.get("dataCriacao")).thenReturn(path);
        when(cb.lessThanOrEqualTo(any(), any(LocalDateTime.class))).thenReturn(predicate);

        var spec = SessaoVotacaoSpecification.hasPautaDataCriacaoTo(to);
        Predicate p = spec.toPredicate(root, query, cb);

        assertNotNull(p);
        assertEquals(predicate, p);
        verify(root).join("pauta");
        verify(cb).lessThanOrEqualTo(any(), any(LocalDateTime.class));
    }

    @Test
    void hasStatus_null_returnsNull() {
        var spec = SessaoVotacaoSpecification.hasStatus(null);
        assertNotNull(spec);
        Predicate p = spec.toPredicate(root, query, cb);
        assertNull(p);
    }

    @Test
    void hasStatus_aberta_returnsAndPredicate() {

        when(cb.lessThanOrEqualTo(any(), any(LocalDateTime.class))).thenReturn(predicate);
        when(cb.greaterThan(any(), any(LocalDateTime.class))).thenReturn(predicate);
        when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        var spec = SessaoVotacaoSpecification.hasStatus(StatusSessao.ABERTA);

        Predicate p = spec.toPredicate(root, query, cb);
        assertNotNull(p);
        verify(cb).lessThanOrEqualTo(any(), any(LocalDateTime.class));
        verify(cb).greaterThan(any(), any(LocalDateTime.class));
        verify(cb).and(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void hasStatus_encerrada_returnsNotPredicate() {
        when(cb.lessThanOrEqualTo(any(), any(LocalDateTime.class))).thenReturn(predicate);
        when(cb.greaterThan(any(), any(LocalDateTime.class))).thenReturn(predicate);
        when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cb.not(any(Predicate.class))).thenReturn(predicate);

        var spec = SessaoVotacaoSpecification.hasStatus(StatusSessao.ENCERRADA);
        Predicate p = spec.toPredicate(root, query, cb);

        assertNotNull(p);
        verify(cb).not(any(Predicate.class));
    }
}
