package com.kaio.limbus_api.controller;

import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.entity.Tag;
import com.kaio.limbus_api.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import com.kaio.limbus_api.assembler.TagModelAssembler;
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
@RequestMapping("/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Gerenciamento das Tags temáticas das Identities, como 'The House of Spiders' ou 'The Pinky'")
public class TagController {

    private final TagService tagService;
    private final TagModelAssembler assembler;
    private final PagedResourcesAssembler<Tag> pagedAssembler;

    public TagController(TagService tagService,
            TagModelAssembler assembler,
            PagedResourcesAssembler<Tag> pagedAssembler) {
        this.tagService = tagService;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Operation(
            summary = "Lista todas as Tags (paginado)",
            description = "Retorna uma página com as Tags cadastradas. Paginação via page (começa em 0), size (padrão 10) e sort (ex.: sort=nome,asc)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de Tags retornada com sucesso (vazia se não houver nenhuma)")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Tag>>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(tagService.listar(pageable), assembler));
    }

    @Operation(
            summary = "Busca Tags pelo nome (paginado)",
            description = "Consulta personalizada: retorna as Tags cujo nome contém o texto informado, sem diferenciar maiúsculas de minúsculas. Ex.: nome=spider encontra 'The House of Spiders'."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página com as Tags encontradas (vazia se nenhuma bater com o texto)"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'nome' ausente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Parâmetro ausente", value = ErrorExamples.BAD_REQUEST_PARAM_NOME)))
    })
    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<Tag>>> buscarPorNome(
            @Parameter(description = "Texto a procurar dentro do nome", example = "spider")
            @RequestParam String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(tagService.buscarPorNome(nome, pageable), assembler));
    }

    @Operation(
            summary = "Busca uma Tag pelo id",
            description = "Retorna os dados de uma única Tag a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag encontrada"),
            @ApiResponse(responseCode = "400", description = "Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Nenhuma Tag com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_TAG)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Tag>> buscarPorId(
            @Parameter(description = "Id da Tag", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(tagService.buscarPorId(id)));
    }

    @Operation(
            summary = "Cadastra uma nova Tag",
            description = "Cria uma Tag. O nome é obrigatório e único. Cadastre as Tags antes de criar Identities que as usem."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tag criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (nome em branco) ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "409", description = "Já existe uma Tag com esse nome",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<Tag>> criar(@RequestBody @Valid Tag tag) {
        EntityModel<Tag> model = assembler.toModel(tagService.criar(tag));
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @Operation(
            summary = "Atualiza uma Tag",
            description = "Substitui o nome de uma Tag existente. As Identities que a usam passam a exibir o novo nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, id não numérico ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Nenhuma Tag com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_TAG))),
            @ApiResponse(responseCode = "409", description = "Já existe outra Tag com esse nome",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Tag>> atualizar(
            @Parameter(description = "Id da Tag a atualizar", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid Tag tag) {
        return ResponseEntity.ok(assembler.toModel(tagService.atualizar(id, tag)));
    }

    @Operation(
            summary = "Remove uma Tag",
            description = "Exclui uma Tag. Só é possível se nenhuma Identity a estiver usando."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag removida com sucesso (sem corpo)"),
            @ApiResponse(responseCode = "400", description = "Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Nenhuma Tag com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_TAG))),
            @ApiResponse(responseCode = "409", description = "A Tag está vinculada a uma ou mais Identities",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id da Tag a remover", example = "1")
            @PathVariable Long id) {
        tagService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}