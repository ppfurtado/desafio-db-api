package com.ppfurtado.desafiovotacao.client;

public interface CpfValidationFacade {

    CpfValidationResponse validateCpf(String cpf);

    boolean isAbleToVote(String cpf);
}
