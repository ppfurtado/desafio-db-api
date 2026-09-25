package com.ppfurtado.desafiovotacao.api.v1.controller;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.PautaCreateRequest;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.PautaResponse;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.service.PautaService;
import com.ppfurtado.desafiovotacao.service.ResultadoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "Endpoints para gerenciamento e consulta de pautas de assembleia")
public class PautaController {

    private final PautaService pautaService;
    private final ResultadoVotacaoService resultadoVotacaoService;

    @PostMapping
    @Operation(summary = "Cadastrar uma nova pauta", description = "Cria uma nova pauta para deliberação na assembleia cooperativa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    public ResponseEntity<PautaResponse> cadastrar(@Valid @RequestBody PautaCreateRequest request) {
        Pauta pauta = pautaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PautaResponse.fromEntity(pauta));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Recupera os detalhes de uma pauta pelo seu identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta encontrada"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable Long id) {
        Pauta pauta = pautaService.buscarPorId(id);
        return ResponseEntity.ok(PautaResponse.fromEntity(pauta));
    }

    @GetMapping
    @Operation(summary = "Listar pautas cadastradas", description = "Lista todas as pautas com suporte a paginação.")
    public ResponseEntity<Page<PautaResponse>> listar(@ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<PautaResponse> response = pautaService.listar(pageable).map(PautaResponse::fromEntity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/resultado")
    @Operation(summary = "Contabilizar e obter o resultado da votação de uma pauta", description = "Retorna a apuração dos votos (SIM, NÃO, totais e percentuais) e o resultado final da pauta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado apurado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada")
    })
    public ResponseEntity<ResultadoVotacaoResponse> obterResultado(@PathVariable Long id) {
        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contabilizarResultadoPorPauta(id);
        return ResponseEntity.ok(resultado);
    }
}
