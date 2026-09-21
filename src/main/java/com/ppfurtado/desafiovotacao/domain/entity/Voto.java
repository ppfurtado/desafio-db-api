package com.ppfurtado.desafiovotacao.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "tb_voto",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_sessao_associado", columnNames = {"sessao_id", "associado_cpf"})
    },
    indexes = {
        @Index(name = "idx_voto_sessao", columnList = "sessao_id"),
        @Index(name = "idx_voto_sessao_opcao", columnList = "sessao_id, opcao_voto")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoVotacao sessao;

    @Column(name = "associado_cpf", nullable = false, length = 20)
    private String associadoCpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "opcao_voto", nullable = false, length = 10)
    private OpcaoVoto opcaoVoto;

    @Column(name = "data_voto", nullable = false, updatable = false)
    private LocalDateTime dataVoto;

    @PrePersist
    public void prePersist() {
        if (this.dataVoto == null) {
            this.dataVoto = LocalDateTime.now();
        }
    }
}
