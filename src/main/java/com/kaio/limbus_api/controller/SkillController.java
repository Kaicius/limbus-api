package com.kaio.limbus_api.controller;

import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.dto.SkillRequest;
import com.kaio.limbus_api.entity.Skill;
import com.kaio.limbus_api.enums.Sin;
import com.kaio.limbus_api.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.kaio.limbus_api.assembler.SkillModelAssembler;
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
@Tag(name = "Skills", description = "Skills das Identities: três ataques e uma defesa, cada uma com pecado, moedas e variantes")
public class SkillController {

    private final SkillService service;
    private final SkillModelAssembler assembler;
    private final PagedResourcesAssembler<Skill> pagedAssembler;

    public SkillController(SkillService service,
            SkillModelAssembler assembler,
            PagedResourcesAssembler<Skill> pagedAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Operation(
            summary = "Lista as Skills (paginado)",
            description = "Retorna uma página com as Skills cadastradas. Paginação via page (começa em 0), size (padrão 10) e sort (ex.: sort=id,desc)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Página retornada com sucesso (vazia se não houver nenhum registro)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Campo de ordenação (sort) inexistente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Ordenação inválida", value = ErrorExamples.BAD_REQUEST_SORT)))
    })
    @GetMapping("/skills")
    public ResponseEntity<PagedModel<EntityModel<Skill>>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(service.listar(pageable), assembler));
    }

    @Operation(
            summary = "Lista Skills por pecado (paginado)",
            description = "Consulta personalizada: retorna as Skills de um determinado pecado (WRATH, LUST, SLOTH, GLOOM, GLUTTONY, ENVY ou PRIDE)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Página com as Skills do pecado (vazia se não houver nenhuma)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Parâmetro 'sin' ausente ou com valor que não é um pecado válido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Pecado inválido", value = ErrorExamples.BAD_REQUEST_SIN)))
    })
    @GetMapping("/skills/search/by-sin")
    public ResponseEntity<PagedModel<EntityModel<Skill>>> buscarPorSin(
            @Parameter(description = "Pecado da Skill", example = "GLOOM")
            @RequestParam Sin sin,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(service.buscarPorSin(sin, pageable), assembler));
    }

    @Operation(
            summary = "Busca a Skill pelo id",
            description = "Retorna a Skill a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Skill encontrado(a)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Skill com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SKILL)))
    })
    @GetMapping("/skills/{id}")
    public ResponseEntity<EntityModel<Skill>> buscarPorId(
            @Parameter(description = "Id da Skill", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorId(id)));
    }

    @Operation(
            summary = "Cadastra uma Skill para uma Identity",
            description = "Cria uma Skill vinculada à Identity da URL. Uma Identity pode ter várias Skills. A Identity precisa existir antes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created – Skill criado(a) com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos (slot ou pecado inexistente, variante ou moedas fora de 1 a 10, nome ou efeito em branco) ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Not Found – A Identity da URL não existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_IDENTITY)))
    })
    @PostMapping("/identities/{identityId}/skills")
    public ResponseEntity<EntityModel<Skill>> criar(
            @Parameter(description = "Id da Identity dona", example = "1")
            @PathVariable Long identityId,
            @RequestBody @Valid SkillRequest request) {
        Skill criado = service.criar(identityId, request.toEntity());
        EntityModel<Skill> model = assembler.toModel(criado);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @Operation(
            summary = "Atualiza a Skill",
            description = "Substitui todos os dados de uma Skill existente. A Identity dona não pode ser alterada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Skill atualizado(a) com sucesso"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Dados inválidos, id não numérico ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Skill com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SKILL)))
    })
    @PutMapping("/skills/{id}")
    public ResponseEntity<EntityModel<Skill>> atualizar(
            @Parameter(description = "Id da Skill a atualizar", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid SkillRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.atualizar(id, request.toEntity())));
    }

    @Operation(
            summary = "Remove a Skill",
            description = "Exclui a Skill. A Identity continua existindo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content – Skill removido(a) com sucesso (sem corpo)"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhum(a) Skill com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SKILL)))
    })
    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id da Skill a remover", example = "1")
            @PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
