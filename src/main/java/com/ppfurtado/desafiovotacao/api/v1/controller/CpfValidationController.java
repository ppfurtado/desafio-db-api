package com.ppfurtado.desafiovotacao.api.v1.controller;

import com.ppfurtado.desafiovotacao.client.CpfValidationFacade;
import com.ppfurtado.desafiovotacao.client.CpfValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Validação de Usuário/CPF (Bônus 1)", description = "Serviço externo simulado para validação de elegibilidade de voto por CPF")
public class CpfValidationController {

    private final CpfValidationFacade cpfValidationFacade;

    @GetMapping({"/v1/users/{cpf}", "/users/{cpf}"})
    @Operation(summary = "Verificar status de voto do CPF", description = "Simula a validação de CPF. Retorna 404 se inválido, ou ABLE_TO_VOTE / UNABLE_TO_VOTE se válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CPF válido e verificado"),
            @ApiResponse(responseCode = "404", description = "CPF inválido")
    })
    public ResponseEntity<CpfValidationResponse> validarCpf(@PathVariable String cpf) {
        CpfValidationResponse response = cpfValidationFacade.validateCpf(cpf);
        return ResponseEntity.ok(response);
    }
}
