package com.ppfurtado.desafiovotacao.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_sessao_votacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_encerramento", nullable = false)
    private LocalDateTime dataEncerramento;

    @Column(name = "duracao_minutos", nullable = false)
    private Long duracaoMinutos;

    public boolean isAberta() {
        LocalDateTime now = LocalDateTime.now();
        return (now.isEqual(dataAbertura) || now.isAfter(dataAbertura)) && now.isBefore(dataEncerramento);
    }

    public StatusSessao getStatus() {
        return isAberta() ? StatusSessao.ABERTA : StatusSessao.ENCERRADA;
    }
}
