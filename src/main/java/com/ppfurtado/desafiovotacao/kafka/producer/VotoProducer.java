package com.ppfurtado.desafiovotacao.kafka.producer;

import com.ppfurtado.desafiovotacao.kafka.event.VotoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VotoProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.votos:votos}")
    private String votesTopic;

    public void publishVoto(VotoEvent event) {
        kafkaTemplate.send(votesTopic, String.valueOf(event.getVotoId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao publicar evento de voto ID {}: {}", event.getVotoId(), ex.getMessage());
                    }
                });
    }
}
