package com.kaio.limbus_api.controller;

import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.entity.Sinner;
import com.kaio.limbus_api.service.SinnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.kaio.limbus_api.assembler.SinnerModelAssembler;
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
@RequestMapping("/sinners")
@Tag(name = "Sinners", description = "Gerenciamento dos 12 Sinners da Limbus Company, os personagens jogáveis aos quais as Identities pertencem")
public class SinnerController {

    private final SinnerService sinnerService;
    private final SinnerModelAssembler assembler;
    private final PagedResourcesAssembler<Sinner> pagedAssembler;

    public SinnerController(SinnerService sinnerService,
            SinnerModelAssembler assembler,
            PagedResourcesAssembler<Sinner> pagedAssembler) {
        this.sinnerService = sinnerService;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Operation(
            summary = "Lista todos os Sinners (paginado)",
            description = "Retorna uma página com os Sinners cadastrados. Paginação via page (começa em 0), size e sort (ex.: sort=nome,asc)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de Sinners retornada com sucesso (vazia se não houver nenhum cadastrado)")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Sinner>>> listar(
            @ParameterObject @PageableDefault(size = 12, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(sinnerService.listar(pageable), assembler));
    }

    @Operation(
            summary = "Busca Sinners pelo nome (paginado)",
            description = "Consulta personalizada: retorna os Sinners cujo nome contém o texto informado, sem diferenciar maiúsculas de minúsculas. Ex.: nome=yi encontra 'Yi Sang'."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página com os Sinners encontrados (vazia se nenhum bater com o texto)"),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'nome' ausente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Parâmetro ausente", value = ErrorExamples.BAD_REQUEST_PARAM_NOME)))
    })
    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<Sinner>>> buscarPorNome(
            @Parameter(description = "Texto a procurar dentro do nome", example = "yi")
            @RequestParam String nome,
            @ParameterObject @PageableDefault(size = 12, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(sinnerService.buscarPorNome(nome, pageable), assembler));
    }

    @Operation(
            summary = "Busca um Sinner pelo id",
            description = "Retorna os dados de um único Sinner a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sinner encontrado"),
            @ApiResponse(responseCode = "400", description = "Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Nenhum Sinner com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SINNER)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Sinner>> buscarPorId(
            @Parameter(description = "Id do Sinner", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(sinnerService.buscarPorId(id)));
    }

    @Operation(
            summary = "Cadastra um novo Sinner",
            description = "Cria um Sinner. O nome é obrigatório, único e deve ter entre 2 e 100 caracteres."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sinner criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (nome em branco, fora de 2 a 100 caracteres) ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "409", description = "Já existe um Sinner com esse nome",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<Sinner>> criar(@RequestBody @Valid Sinner sinner) {
        EntityModel<Sinner> model = assembler.toModel(sinnerService.criar(sinner));
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @Operation(
            summary = "Atualiza um Sinner",
            description = "Substitui o nome de um Sinner existente. As mesmas regras de validação do cadastro se aplicam."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sinner atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, id não numérico ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Dados inválidos", value = ErrorExamples.BAD_REQUEST_VALIDATION))),
            @ApiResponse(responseCode = "404", description = "Nenhum Sinner com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SINNER))),
            @ApiResponse(responseCode = "409", description = "Já existe outro Sinner com esse nome",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Sinner>> atualizar(
            @Parameter(description = "Id do Sinner a atualizar", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid Sinner sinner) {
        return ResponseEntity.ok(assembler.toModel(sinnerService.atualizar(id, sinner)));
    }

    @Operation(
            summary = "Remove um Sinner",
            description = "Exclui um Sinner. Só é possível se ele não tiver Identities vinculadas: remova ou mova as Identities antes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sinner removido com sucesso (sem corpo)"),
            @ApiResponse(responseCode = "400", description = "Id inválido (não numérico)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Nenhum Sinner com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_SINNER))),
            @ApiResponse(responseCode = "409", description = "O Sinner possui Identities vinculadas e não pode ser removido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Conflito", value = ErrorExamples.CONFLICT)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Id do Sinner a remover", example = "1")
            @PathVariable Long id) {
        sinnerService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}