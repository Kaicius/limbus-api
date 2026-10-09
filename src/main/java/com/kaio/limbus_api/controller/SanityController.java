package com.kaio.limbus_api.controller;

import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.dto.SanityRequest;
import com.kaio.limbus_api.entity.Sanity;
import com.kaio.limbus_api.service.SanityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.kaio.limbus_api.assembler.SanityModelAssembler;
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
@Tag(name = "Sanity", description = "Informações de Sanity de cada Identity: descrição do Panic e o que aumenta ou diminui a Sanity. Cada Identity tem no máximo uma")
public class SanityController {

    private final SanityService service;
    private final SanityModelAssembler assembler;
    private final PagedResourcesAssembler<Sanity> pagedAssembler;

    public SanityController(SanityService service,
            SanityModelAssembler assembler,
            PagedResourcesAssembler<Sanity> pagedAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Operation(
            summary = "Lista as Sanities (paginado)",
            description = "Retorna uma página com as Sanities cadastradas. Paginação via page (começa em 0), size (padrão 10) e sort (ex.: sort=id,desc)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Página retornada com sucesso (vazia se não houver nenhum registro)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Campo de ordenação (sort) inexistente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Ordenação inválida", value = ErrorExamples.BAD_REQUEST_SORT)))
    })
    @GetMapping("/sanities")
    public ResponseEntity<PagedModel<EntityModel<Sanity>>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(service.listar(pageable), assembler));
    }

    @Operation(
            summary = "Busca a Sanity de uma Identity",
            description = "Consulta personalizada: retorna a Sanity (Panic, fatores que aumentam e que diminuem) vinculada à Identity informada. Cada Identity tem no máximo uma Sanity."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Sanity da Identity encontrada"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id da Identity inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – A Identity não existe ou ainda não tem Sanity cadastrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SANITY_IDENTITY)))
    })
    @GetMapping("/identities/{identityId}/sanity")
    public ResponseEntity<EntityModel<Sanity>> buscarPorIdentity(
            @Parameter(description = "Id da Identity", example = "1")
            @PathVariable Long identityId) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorIdentity(identityId)));
    }

    @Operation(
            summary = "Busca a Sanity pelo id",
            description = "Retorna a Sanity a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Sanity encontrado(a)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Sanity com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SANITY)))
    })
    @GetMapping("/sanities/{id}")
    public ResponseEntity<EntityModel<Sanity>> buscarPorId(
            @Parameter(description = "Id da Sanity", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorId(id)));
    }

    @Operation(
            summary = "Cadastra uma Sanity para uma Identity",
            description = "Cria a Sanity da Identity da URL. Cada Identity só pode ter uma Sanity; para alterar, use o PUT. A Identity precisa existir antes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created – Sanity criado(a) com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos (campo em branco ou acima de 2000 caracteres) ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Not Found – A Identity da URL não existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_IDENTITY))),
            @ApiResponse(responseCode = "409", description = "Conflict – A Identity já possui uma Sanity cadastrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @PostMapping("/identities/{identityId}/sanity")
    public ResponseEntity<EntityModel<Sanity>> criar(
            @Parameter(description = "Id da Identity dona", example = "1")
            @PathVariable Long identityId,
            @RequestBody @Valid SanityRequest request) {
        Sanity criado = service.criar(identityId, request.toEntity());
        EntityModel<Sanity> model = assembler.toModel(criado);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @Operation(
            summary = "Atualiza a Sanity",
            description = "Substitui o Panic e os fatores de Sanity de um registro existente. A Identity dona não pode ser alterada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Sanity atualizado(a) com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos, id não numérico ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Sanity com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SANITY)))
    })
    @PutMapping("/sanities/{id}")
    public ResponseEntity<EntityModel<Sanity>> atualizar(
            @Parameter(description = "Id da Sanity a atualizar", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid SanityRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.atualizar(id, request.toEntity())));
    }

    @Operation(
            summary = "Remove a Sanity",
            description = "Exclui a Sanity. A Identity continua existindo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content – Sanity removido(a) com sucesso (sem corpo)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Sanity com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SANITY)))
    })
    @DeleteMapping("/sanities/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id da Sanity a remover", example = "1")
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
