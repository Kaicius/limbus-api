package com.kaio.limbus_api.controller;

import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.dto.IdentityRequest;
import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.service.IdentityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.kaio.limbus_api.assembler.IdentityModelAssembler;
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
@Tag(name = "Identities", description = "Gerenciamento das Identities, as versões jogáveis de cada Sinner, com raridade, uptie e Tags")
public class IdentityController {

    private final IdentityService identityService;
    private final IdentityModelAssembler assembler;
    private final PagedResourcesAssembler<Identity> pagedAssembler;

    public IdentityController(IdentityService identityService,
            IdentityModelAssembler assembler,
            PagedResourcesAssembler<Identity> pagedAssembler) {
        this.identityService = identityService;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Operation(
            summary = "Lista todas as Identities (paginado)",
            description = "Retorna uma página com as Identities cadastradas, já com o Sinner dono e as Tags. Paginação via page (começa em 0), size (padrão 10) e sort (ex.: sort=nome,asc)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Página de Identities retornada com sucesso (vazia se não houver nenhuma)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Campo de ordenação (sort) inexistente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Ordenação inválida", value = ErrorExamples.BAD_REQUEST_SORT)))
    })
    @GetMapping("/identities")
    public ResponseEntity<PagedModel<EntityModel<Identity>>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(identityService.listar(pageable), assembler));
    }

    @Operation(
            summary = "Lista as Identities de um Sinner (paginado)",
            description = "Consulta personalizada: retorna as Identities que pertencem ao Sinner informado. Se o Sinner não existir ou não tiver Identities, a página vem vazia."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Página com as Identities do Sinner (vazia se não houver)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id do Sinner inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID)))
    })
    @GetMapping("/sinners/{sinnerId}/identities")
    public ResponseEntity<PagedModel<EntityModel<Identity>>> buscarPorSinner(
            @Parameter(description = "Id do Sinner dono das Identities", example = "2")
            @PathVariable Long sinnerId,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(identityService.buscarPorSinner(sinnerId, pageable), assembler));
    }

    @Operation(
            summary = "Lista as Identities de uma Tag (paginado)",
            description = "Consulta personalizada: retorna as Identities que possuem a Tag de nome exato informado. Ex.: tag=The House of Spiders."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Página com as Identities que possuem a Tag (vazia se nenhuma)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Parâmetro 'tag' ausente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Parâmetro ausente", value = ErrorExamples.BAD_REQUEST_PARAM_TAG)))
    })
    @GetMapping("/identities/search/by-tag")
    public ResponseEntity<PagedModel<EntityModel<Identity>>> buscarPorTag(
            @Parameter(description = "Nome exato da Tag", example = "The House of Spiders")
            @RequestParam String tag,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(identityService.buscarPorTag(tag, pageable), assembler));
    }

    @Operation(
            summary = "Busca uma Identity pelo id",
            description = "Retorna uma única Identity, com o Sinner dono e a lista de Tags."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Identity encontrada"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhuma Identity com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_IDENTITY)))
    })
    @GetMapping("/identities/{id}")
    public ResponseEntity<EntityModel<Identity>> buscarPorId(
            @Parameter(description = "Id da Identity", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(identityService.buscarPorId(id)));
    }

    @Operation(
            summary = "Cadastra uma Identity para um Sinner",
            description = "Cria uma Identity vinculada ao Sinner da URL. O corpo informa nome, uptie (1 a 4), raridade e os ids das Tags (ao menos uma). O Sinner e as Tags precisam existir antes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created – Identity criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos (nome em branco, uptie fora de 1 a 4, raridade inexistente, lista de Tags vazia ou com mais de 10) ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Not Found – O Sinner da URL ou alguma das Tags informadas não existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SINNER))),
            @ApiResponse(responseCode = "409", description = "Conflict – Já existe outra Identity com esse nome",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @PostMapping("/sinners/{sinnerId}/identities")
    public ResponseEntity<EntityModel<Identity>> criar(
            @Parameter(description = "Id do Sinner dono da nova Identity", example = "1")
            @PathVariable Long sinnerId,
            @RequestBody @Valid IdentityRequest request) {
        Identity criada = identityService.criar(sinnerId, request.toEntity(), request.tagIds());
        EntityModel<Identity> model = assembler.toModel(criada);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @Operation(
            summary = "Atualiza uma Identity",
            description = "Substitui nome, uptie, raridade e o conjunto de Tags de uma Identity. O Sinner dono não pode ser alterado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Identity atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos, id não numérico ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Not Found – A Identity ou alguma das Tags informadas não existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_IDENTITY))),
            @ApiResponse(responseCode = "409", description = "Conflict – Já existe outra Identity com esse nome",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @PutMapping("/identities/{id}")
    public ResponseEntity<EntityModel<Identity>> atualizar(
            @Parameter(description = "Id da Identity a atualizar", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid IdentityRequest request) {
        return ResponseEntity.ok(assembler.toModel(identityService.atualizar(id, request.toEntity(), request.tagIds())));
    }

    @Operation(
            summary = "Remove uma Identity",
            description = "Exclui uma Identity. Só é possível se ela não tiver Skills, Passivas, Stats ou Sanity vinculados: remova esses registros antes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content – Identity removida com sucesso (sem corpo)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhuma Identity com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_IDENTITY))),
            @ApiResponse(responseCode = "409", description = "Conflict – A Identity possui Skills, Passivas, Stats ou Sanity vinculados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @DeleteMapping("/identities/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id da Identity a remover", example = "1")
            @PathVariable Long id) {
        identityService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}