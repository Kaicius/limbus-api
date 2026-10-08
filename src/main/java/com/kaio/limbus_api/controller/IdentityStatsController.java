package com.kaio.limbus_api.controller;

import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.dto.IdentityStatsRequest;
import com.kaio.limbus_api.entity.IdentityStats;
import com.kaio.limbus_api.service.IdentityStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.kaio.limbus_api.assembler.IdentityStatsModelAssembler;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Identity Stats", description = "Atributos de combate de cada Identity (HP, velocidade, defesa, limiar de Stagger e resistências). Cada Identity tem no máximo um registro")
public class IdentityStatsController {

    private final IdentityStatsService service;
    private final IdentityStatsModelAssembler assembler;
    private final PagedResourcesAssembler<IdentityStats> pagedAssembler;

    public IdentityStatsController(IdentityStatsService service,
            IdentityStatsModelAssembler assembler,
            PagedResourcesAssembler<IdentityStats> pagedAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Operation(
            summary = "Lista os Stats (paginado)",
            description = "Retorna uma página com os Stats cadastrados. Paginação via page (começa em 0), size (padrão 10) e sort (ex.: sort=id,desc)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página retornada com sucesso (vazia se não houver nenhum registro)")
    })
    @GetMapping("/identity-stats")
    public ResponseEntity<PagedModel<EntityModel<IdentityStats>>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(service.listar(pageable), assembler));
    }

    @Operation(
            summary = "Busca os Stats de uma Identity",
            description = "Consulta personalizada: retorna os Stats vinculados à Identity informada. Cada Identity tem no máximo um registro de Stats (relação um-para-um)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stats da Identity encontrados"),
            @ApiResponse(responseCode = "400", description = "Id da Identity inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "A Identity não existe ou ainda não tem Stats cadastrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_STATS_IDENTITY)))
    })
    @GetMapping("/identities/{identityId}/stats")
    public ResponseEntity<EntityModel<IdentityStats>> buscarPorIdentity(
            @Parameter(description = "Id da Identity", example = "1")
            @PathVariable Long identityId) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorIdentity(identityId)));
    }

    @Operation(
            summary = "Busca o Stats pelo id",
            description = "Retorna o Stats a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stats encontrado(a)"),
            @ApiResponse(responseCode = "400", description = "Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Nenhum(a) Stats com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_STATS)))
    })
    @GetMapping("/identity-stats/{id}")
    public ResponseEntity<EntityModel<IdentityStats>> buscarPorId(
            @Parameter(description = "Id do Stats", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorId(id)));
    }

    @Operation(
            summary = "Cadastra os Stats para uma Identity",
            description = "Cria os Stats da Identity da URL. Cada Identity só pode ter um registro de Stats; para alterar, use o PUT. A Identity precisa existir antes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stats criado(a) com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (HP ou Stagger abaixo de 1, velocidade fora do formato mín-máx, defesa negativa, resistência inexistente) ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "A Identity da URL não existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_IDENTITY))),
            @ApiResponse(responseCode = "409", description = "A Identity já possui Stats cadastrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @PostMapping("/identities/{identityId}/stats")
    public ResponseEntity<EntityModel<IdentityStats>> criar(
            @Parameter(description = "Id da Identity dona", example = "1")
            @PathVariable Long identityId,
            @RequestBody @Valid IdentityStatsRequest request) {
        IdentityStats criado = service.criar(identityId, request.toEntity());
        EntityModel<IdentityStats> model = assembler.toModel(criado);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @Operation(
            summary = "Atualiza o Stats",
            description = "Substitui todos os valores de Stats de um registro existente. A Identity dona não pode ser alterada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stats atualizado(a) com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, id não numérico ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Nenhum(a) Stats com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_STATS)))
    })
    @PutMapping("/identity-stats/{id}")
    public ResponseEntity<EntityModel<IdentityStats>> atualizar(
            @Parameter(description = "Id do Stats a atualizar", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid IdentityStatsRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.atualizar(id, request.toEntity())));
    }

    @Operation(
            summary = "Remove o Stats",
            description = "Exclui o registro de Stats. A Identity continua existindo e pode receber novos Stats depois."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Stats removido(a) com sucesso (sem corpo)"),
            @ApiResponse(responseCode = "400", description = "Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Nenhum(a) Stats com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_STATS)))
    })
    @DeleteMapping("/identity-stats/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id do Stats a remover", example = "1")
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
