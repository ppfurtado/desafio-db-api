package com.ppfurtado.desafiovotacao.client;

import com.ppfurtado.desafiovotacao.domain.exception.CpfInvalidoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class CpfValidationFacadeImpl implements CpfValidationFacade {

    private final Random random = new Random();

    @Override
    public CpfValidationResponse validateCpf(String cpf) {
        String cleanCpf = CpfValidatorUtil.sanitize(cpf);

        if (!CpfValidatorUtil.isValid(cleanCpf)) {
            log.warn("Tentativa de validação com CPF inválido: {}", cpf);
            throw new CpfInvalidoException("CPF inválido: " + cpf);
        }

        // Simula aleatoriedade conforme especificado na Tarefa Bônus 1
        CpfValidationStatus status = random.nextBoolean() 
                ? CpfValidationStatus.ABLE_TO_VOTE 
                : CpfValidationStatus.UNABLE_TO_VOTE;

        log.info("Validação de CPF {} realizada com status: {}", cleanCpf, status);
        return CpfValidationResponse.builder()
                .status(status)
                .build();
    }

    @Override
    public boolean isAbleToVote(String cpf) {
        CpfValidationResponse response = validateCpf(cpf);
        return CpfValidationStatus.ABLE_TO_VOTE.equals(response.getStatus());
    }
}
