package com.ppfurtado.desafiovotacao.kafka.consumer;

import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.Voto;
import com.ppfurtado.desafiovotacao.kafka.event.VotoEvent;
import com.ppfurtado.desafiovotacao.repository.SessaoVotacaoRepository;
import com.ppfurtado.desafiovotacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class VotoConsumer {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    @KafkaListener(topics = "${app.kafka.topic.votos:votos}", groupId = "desafio-votacao-group")
    public void handleVotoEvent(VotoEvent event) {
        try {
            if (event == null) return;
            Long sessaoId = event.getSessaoId();
            String cpf = event.getAssociadoCpf();

            if (votoRepository.existsBySessaoIdAndAssociadoCpf(sessaoId, cpf)) {
                log.info("Voto já existe para sessão {} e associado {} — ignorando", sessaoId, cpf);
                return;
            }
            Optional<SessaoVotacao> sessaoOpt = sessaoVotacaoRepository.findById(sessaoId);
            if (sessaoOpt.isEmpty()) {
                log.warn("Sessão {} não encontrada ao processar evento de voto {}", sessaoId, event.getVotoId());
                return;
            }
            SessaoVotacao sessao = sessaoOpt.get();
            OpcaoVoto opcao = event.getOpcaoVoto();
            Voto voto = Voto.builder()
                    .sessao(sessao)
                    .associadoCpf(cpf)
                    .opcaoVoto(opcao)
                    .dataVoto(event.getDataVoto() != null ? event.getDataVoto() : LocalDateTime.now())
                    .build();
            Voto salvo = votoRepository.save(voto);
            log.info("Voto persistido pelo consumer ID {} (sessao {})", salvo.getId(), sessaoId);
        } catch (Exception e) {
            log.error("Erro ao processar VotoEvent: {}", e.getMessage(), e);
        }
    }
}
