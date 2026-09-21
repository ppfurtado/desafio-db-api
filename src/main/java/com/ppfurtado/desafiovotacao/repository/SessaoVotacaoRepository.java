package com.ppfurtado.desafiovotacao.repository;

import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long>, JpaSpecificationExecutor<SessaoVotacao> {

    List<SessaoVotacao> findByPautaId(Long pautaId);

    boolean existsByPautaId(Long pautaId);
}
