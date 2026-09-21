package com.ppfurtado.desafiovotacao.repository;

import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.domain.entity.Voto;
import com.ppfurtado.desafiovotacao.repository.projection.VotoContabilizadoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsBySessaoIdAndAssociadoCpf(Long sessaoId, String associadoCpf);

    long countBySessaoIdAndOpcaoVoto(Long sessaoId, OpcaoVoto opcaoVoto);

    long countBySessaoId(Long sessaoId);

    @Query("SELECT v.opcaoVoto as opcao, COUNT(v) as total FROM Voto v WHERE v.sessao.id = :sessaoId GROUP BY v.opcaoVoto")
    List<VotoContabilizadoProjection> countVotosBySessaoIdGrouped(@Param("sessaoId") Long sessaoId);
}
