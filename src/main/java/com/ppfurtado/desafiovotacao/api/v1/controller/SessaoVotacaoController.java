package com.ppfurtado.desafiovotacao.api.v1.controller;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.AbrirSessaoRequest;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.SessaoVotacaoResponse;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.service.ResultadoVotacaoService;
import com.ppfurtado.desafiovotacao.service.SessaoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.ppfurtado.desafiovotacao.api.v1.dto.request.SessaoVotacaoFilter;

@RestController
@RequestMapping("/v1/sessoes")
@RequiredArgsConstructor
@Tag(name = "Sessões de Votação", description = "Endpoints para abertura e consulta de sessões de votação")
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;
    private final ResultadoVotacaoService resultadoVotacaoService;

    @PostMapping
    @Operation(summary = "Abrir uma nova sessão de votação em uma pauta", description = "Abre a sessão de votação para a pauta indicada. Se a duração não for informada, o padrão de 1 minuto será utilizado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sessão de votação aberta com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou sessão já existente"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<SessaoVotacaoResponse> abrirSessao(@Valid @RequestBody AbrirSessaoRequest request) {
        SessaoVotacao sessao = sessaoVotacaoService.abrirSessao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SessaoVotacaoResponse.fromEntity(sessao));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sessão de votação por ID", description = "Recupera os dados e o status de uma sessão de votação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão encontrada"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<SessaoVotacaoResponse> buscarPorId(@PathVariable Long id) {
        SessaoVotacao sessao = sessaoVotacaoService.buscarPorId(id);
        return ResponseEntity.ok(SessaoVotacaoResponse.fromEntity(sessao));
    }

    @GetMapping("/{id}/resultado")
    @Operation(summary = "Obter resultado da apuração por ID da sessão", description = "Retorna a apuração dos votos da sessão informada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado apurado"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<ResultadoVotacaoResponse> obterResultado(@PathVariable Long id) {
        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contabilizarResultadoPorSessao(id);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    @Operation(summary = "Listar todas as sessões de votação", description = "Retorna todas as sessões de votação cadastradas com paginação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada")
    })
    public ResponseEntity<Page<SessaoVotacaoResponse>> listarTodas(@ModelAttribute SessaoVotacaoFilter filter, @PageableDefault(size = 20) Pageable pageable) {
        Page<SessaoVotacao> sessoes = sessaoVotacaoService.listarTodas(filter, pageable);
        Page<SessaoVotacaoResponse> response = sessoes.map(SessaoVotacaoResponse::fromEntity);
        return ResponseEntity.ok(response);
    }
}
