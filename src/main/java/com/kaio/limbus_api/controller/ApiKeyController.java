package com.kaio.limbus_api.controller;

import com.kaio.limbus_api.dto.ApiKeyCriadaResponse;
import com.kaio.limbus_api.dto.ApiKeyRequest;
import com.kaio.limbus_api.dto.ApiKeyResponse;
import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.filter.ApiKeyFilter;
import com.kaio.limbus_api.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api-keys")
@Tag(name = "API Keys", description = "Chaves de API: qualquer pessoa gera a sua, cada usuário consulta e revoga a própria, e o administrador gerencia todas")
public class ApiKeyController {

    private final ApiKeyService service;

    public ApiKeyController(ApiKeyService service) {
        this.service = service;
    }

    @Operation(
            summary = "Gera uma chave de API (cadastro)",
            description = "Rota aberta: informe nome e e-mail para receber uma chave. O valor completo aparece somente nesta resposta, "
                    + "porque a API guarda apenas o hash (SHA-256). Envie a chave no cabeçalho X-API-Key para criar, editar e excluir recursos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created – Chave gerada. Copie o campo 'chave' agora"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos (nome fora de 2 a 100 caracteres, e-mail ausente ou malformado) ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_EMAIL)))
    })
    @PostMapping
    public ResponseEntity<ApiKeyCriadaResponse> gerar(@Valid @RequestBody ApiKeyRequest request) {
        ApiKeyCriadaResponse criada = service.gerar(request.nome(), request.email());
        return ResponseEntity.created(URI.create(ApiKeyFilter.ROTA_DA_PROPRIA_CHAVE)).body(criada);
    }

    @Operation(
            summary = "Consulta a própria chave",
            description = "Retorna os dados da chave enviada em X-API-Key (dono, prefixo, criação e último uso), sem o valor dela."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Dados da chave usada na requisição"),
            @ApiResponse(responseCode = "403", description = "Forbidden – A chave de administrador não tem cadastro próprio",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Chave de administrador", value = ErrorExamples.FORBIDDEN_ADMIN_SEM_CADASTRO)))
    })
    @GetMapping("/me")
    public ResponseEntity<ApiKeyResponse> minhaChave(
            @Parameter(hidden = true)
            @RequestAttribute(ApiKeyFilter.ATRIBUTO_ID_DA_CHAVE) Long apiKeyId) {
        return ResponseEntity.ok(ApiKeyResponse.de(service.buscarPorId(apiKeyId)));
    }

    @Operation(
            summary = "Revoga a própria chave",
            description = "Desativa a chave enviada em X-API-Key. A partir daí ela deixa de ser aceita (401). Para voltar a escrever na API, gere outra."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content – Chave revogada"),
            @ApiResponse(responseCode = "403", description = "Forbidden – A chave de administrador não tem cadastro próprio",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Chave de administrador", value = ErrorExamples.FORBIDDEN_ADMIN_SEM_CADASTRO)))
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> revogarMinhaChave(
            @Parameter(hidden = true)
            @RequestAttribute(ApiKeyFilter.ATRIBUTO_ID_DA_CHAVE) Long apiKeyId) {
        service.revogar(apiKeyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Lista todas as chaves (administrador)",
            description = "Retorna todas as chaves cadastradas, ativas e revogadas, sem o valor delas. Exige a chave de administrador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Lista de chaves (vazia se nenhuma foi gerada)")
    })
    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listar() {
        return ResponseEntity.ok(service.listar().stream().map(ApiKeyResponse::de).toList());
    }

    @Operation(
            summary = "Busca uma chave pelo id (administrador)",
            description = "Retorna os dados de uma chave (sem o valor dela). Exige a chave de administrador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Chave encontrada"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhuma chave com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_API_KEY)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiKeyResponse> buscarPorId(
            @Parameter(description = "Id da chave", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiKeyResponse.de(service.buscarPorId(id)));
    }

    @Operation(
            summary = "Revoga a chave de um usuário (administrador)",
            description = "Desativa a chave: ela continua na lista, mas deixa de ser aceita (401). Revogar uma chave já revogada não muda nada. Exige a chave de administrador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Chave revogada"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhuma chave com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_API_KEY)))
    })
    @PutMapping("/{id}/revogar")
    public ResponseEntity<ApiKeyResponse> revogar(
            @Parameter(description = "Id da chave", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiKeyResponse.de(service.revogar(id)));
    }

    @Operation(
            summary = "Exclui uma chave (administrador)",
            description = "Remove a chave definitivamente. Exige a chave de administrador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content – Chave excluída"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhuma chave com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_API_KEY)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Id da chave", example = "1")
            @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
