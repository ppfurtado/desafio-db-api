package com.ppfurtado.desafiovotacao.api.v1.controller;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.VotoCreateRequest;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.VotoResponse;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.service.SessaoVotacaoService;
import com.ppfurtado.desafiovotacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Votação", description = "Endpoints para registro de votos de associados")
public class VotoController {

    private final VotoService votoService;
    private final SessaoVotacaoService sessaoVotacaoService;

    @PostMapping("/v1/sessoes/{sessaoId}/votos")
    @Operation(summary = "Registrar voto de associado na sessão", description = "Registra a opção de voto (SIM/NAO) do associado na sessão especificada. Cada associado pode votar apenas uma vez por pauta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do voto inválidos"),
            @ApiResponse(responseCode = "403", description = "Associado não habilitado para votar"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada"),
            @ApiResponse(responseCode = "409", description = "Associado já votou nesta sessão"),
            @ApiResponse(responseCode = "422", description = "Sessão de votação já está encerrada")
    })
    public ResponseEntity<Void> registrarVotoNaSessao(
            @PathVariable Long sessaoId,
            @Valid @RequestBody VotoCreateRequest request) {

        votoService.registrarVoto(sessaoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/v1/pautas/{pautaId}/votos")
    @Operation(summary = "Registrar voto de associado informando o ID da pauta", description = "Endpoint de conveniência que localiza a sessão ativa da pauta e registra o voto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do voto inválidos"),
            @ApiResponse(responseCode = "403", description = "Associado não habilitado para votar"),
            @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada"),
            @ApiResponse(responseCode = "409", description = "Associado já votou nesta pauta"),
            @ApiResponse(responseCode = "422", description = "Sessão de votação já está encerrada")
    })
    public ResponseEntity<VotoResponse> registrarVotoNaPauta(
            @PathVariable Long pautaId,
            @Valid @RequestBody VotoCreateRequest request) {

        SessaoVotacao sessao = sessaoVotacaoService.buscarPorPautaId(pautaId);
        votoService.registrarVoto(sessao.getId(), request);
        return ResponseEntity.accepted().build();
    }
}
