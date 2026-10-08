package com.kaio.limbus_api.controller;

import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.dto.PassiveRequest;
import com.kaio.limbus_api.entity.Passive;
import com.kaio.limbus_api.enums.PassivaType;
import com.kaio.limbus_api.service.PassiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.kaio.limbus_api.assembler.PassiveModelAssembler;
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
@Tag(name = "Passives", description = "Passivas das Identities, de combate (BATTLE) ou de suporte (SUPPORT)")
public class PassiveController {

    private final PassiveService service;
    private final PassiveModelAssembler assembler;
    private final PagedResourcesAssembler<Passive> pagedAssembler;

    public PassiveController(PassiveService service,
            PassiveModelAssembler assembler,
            PagedResourcesAssembler<Passive> pagedAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Operation(
            summary = "Lista as Passivas (paginado)",
            description = "Retorna uma página com as Passivas cadastradas. Paginação via page (começa em 0), size (padrão 10) e sort (ex.: sort=id,desc)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Página retornada com sucesso (vazia se não houver nenhum registro)")
    })
    @GetMapping("/passives")
    public ResponseEntity<PagedModel<EntityModel<Passive>>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(service.listar(pageable), assembler));
    }

    @Operation(
            summary = "Lista Passivas por tipo (paginado)",
            description = "Consulta personalizada: retorna as Passivas de um tipo, BATTLE (combate) ou SUPPORT (suporte)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Página com as Passivas do tipo (vazia se não houver nenhuma)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Parâmetro 'tipo' ausente ou com valor que não é um tipo válido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Tipo inválido", value = ErrorExamples.BAD_REQUEST_TIPO)))
    })
    @GetMapping("/passives/search/by-tipo")
    public ResponseEntity<PagedModel<EntityModel<Passive>>> buscarPorTipo(
            @Parameter(description = "Tipo da Passiva", example = "BATTLE")
            @RequestParam PassivaType tipo,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(service.buscarPorTipo(tipo, pageable), assembler));
    }

    @Operation(
            summary = "Busca a Passiva pelo id",
            description = "Retorna a Passiva a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Passiva encontrado(a)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Passiva com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_PASSIVE)))
    })
    @GetMapping("/passives/{id}")
    public ResponseEntity<EntityModel<Passive>> buscarPorId(
            @Parameter(description = "Id da Passiva", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorId(id)));
    }

    @Operation(
            summary = "Cadastra uma Passiva para uma Identity",
            description = "Cria uma Passiva vinculada à Identity da URL. Uma Identity pode ter várias Passivas. A Identity precisa existir antes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created – Passiva criado(a) com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos (tipo inexistente, nome ou descrição em branco) ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Not Found – A Identity da URL não existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_IDENTITY)))
    })
    @PostMapping("/identities/{identityId}/passives")
    public ResponseEntity<EntityModel<Passive>> criar(
            @Parameter(description = "Id da Identity dona", example = "1")
            @PathVariable Long identityId,
            @RequestBody @Valid PassiveRequest request) {
        Passive criado = service.criar(identityId, request.toEntity());
        EntityModel<Passive> model = assembler.toModel(criado);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @Operation(
            summary = "Atualiza a Passiva",
            description = "Substitui todos os dados de uma Passiva existente. A Identity dona não pode ser alterada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Passiva atualizado(a) com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos, id não numérico ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Passiva com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_PASSIVE)))
    })
    @PutMapping("/passives/{id}")
    public ResponseEntity<EntityModel<Passive>> atualizar(
            @Parameter(description = "Id da Passiva a atualizar", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid PassiveRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.atualizar(id, request.toEntity())));
    }

    @Operation(
            summary = "Remove a Passiva",
            description = "Exclui a Passiva. A Identity continua existindo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content – Passiva removido(a) com sucesso (sem corpo)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Passiva com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_PASSIVE)))
    })
    @DeleteMapping("/passives/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id da Passiva a remover", example = "1")
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
