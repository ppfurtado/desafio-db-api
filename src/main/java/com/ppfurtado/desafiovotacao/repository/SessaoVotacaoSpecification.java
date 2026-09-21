package com.ppfurtado.desafiovotacao.repository;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.SessaoVotacaoFilter;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.StatusSessao;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SessaoVotacaoSpecification {

    public static final String PAUTA = "pauta";
    public static final String DATA_ENCERRAMENTO = "dataEncerramento";
    public static final String DATA_ABERTURA = "dataAbertura";

    private SessaoVotacaoSpecification() {
    }

    public static Specification<SessaoVotacao> of(SessaoVotacaoFilter filter) {
        if (filter == null) return null;

        return Specification.where(hasPautaTitulo(filter.getPautaTitulo()))
                .and(hasPautaDataCriacaoFrom(filter.getPautaDataCriacaoFrom()))
                .and(hasPautaDataCriacaoTo(filter.getPautaDataCriacaoTo()))
                .and(hasDataAberturaFrom(filter.getDataAberturaFrom()))
                .and(hasDataAberturaTo(filter.getDataAberturaTo()))
                .and(hasDataEncerramentoFrom(filter.getDataEncerramentoFrom()))
                .and(hasDataEncerramentoTo(filter.getDataEncerramentoTo()))
                .and(hasDuracaoMinutosMin(filter.getDuracaoMinutosMin()))
                .and(hasDuracaoMinutosMax(filter.getDuracaoMinutosMax()))
                .and(hasStatus(filter.getStatus()));
    }

    public static Specification<SessaoVotacao> hasPautaTitulo(String titulo) {
        return (root, query, cb) -> {
            if (titulo == null) return null;
            Join<SessaoVotacao, Pauta> join = root.join(PAUTA);
            return cb.like(cb.lower(join.get("titulo")), "%" + titulo.toLowerCase() + "%");
        };
    }

    public static Specification<SessaoVotacao> hasPautaDataCriacaoFrom(LocalDate from) {
        return (root, query, cb) -> {
            if (from == null) return null;
            Join<SessaoVotacao, Pauta> join = root.join(PAUTA);
            LocalDateTime fromDt = from.atStartOfDay();
            return cb.greaterThanOrEqualTo(join.get("dataCriacao"), fromDt);
        };
    }

    public static Specification<SessaoVotacao> hasPautaDataCriacaoTo(LocalDate to) {
        return (root, query, cb) -> {
            if (to == null) return null;
            Join<SessaoVotacao, Pauta> join = root.join(PAUTA);
            LocalDateTime toDt = to.atTime(LocalTime.MAX);
            return cb.lessThanOrEqualTo(join.get("dataCriacao"), toDt);
        };
    }

    public static Specification<SessaoVotacao> hasDataAberturaFrom(LocalDateTime from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get(DATA_ABERTURA), from);
    }

    public static Specification<SessaoVotacao> hasDataAberturaTo(LocalDateTime to) {
        return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get(DATA_ABERTURA), to);
    }

    public static Specification<SessaoVotacao> hasDataEncerramentoFrom(LocalDateTime from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get(DATA_ENCERRAMENTO), from);
    }

    public static Specification<SessaoVotacao> hasDataEncerramentoTo(LocalDateTime to) {
        return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get(DATA_ENCERRAMENTO), to);
    }

    public static Specification<SessaoVotacao> hasDuracaoMinutosMin(Long min) {
        return (root, query, cb) -> min == null ? null : cb.greaterThanOrEqualTo(root.get("duracaoMinutos"), min);
    }

    public static Specification<SessaoVotacao> hasDuracaoMinutosMax(Long max) {
        return (root, query, cb) -> max == null ? null : cb.lessThanOrEqualTo(root.get("duracaoMinutos"), max);
    }

    public static Specification<SessaoVotacao> hasStatus(StatusSessao status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            LocalDateTime now = LocalDateTime.now();
            Predicate aberta = cb.and(cb.lessThanOrEqualTo(root.get(DATA_ABERTURA), now), cb.greaterThan(root.get(DATA_ENCERRAMENTO), now));
            if (status == StatusSessao.ABERTA) {
                return aberta;
            } else {
                return cb.not(aberta);
            }
        };
    }
}
