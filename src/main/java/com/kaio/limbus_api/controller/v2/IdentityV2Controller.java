package com.kaio.limbus_api.controller.v2;

import com.kaio.limbus_api.config.ApiVersionConfig;
import com.kaio.limbus_api.controller.IdentityController;
import com.kaio.limbus_api.controller.SinnerController;
import com.kaio.limbus_api.dto.ErrorExamples;
import com.kaio.limbus_api.dto.ErrorResponse;
import com.kaio.limbus_api.dto.v2.IdentityFichaResponse;
import com.kaio.limbus_api.service.IdentityFichaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Identities")
public class IdentityV2Controller {

    private final IdentityFichaService fichaService;

    public IdentityV2Controller(IdentityFichaService fichaService) {
        this.fichaService = fichaService;
    }

    @Operation(
            summary = "Busca a ficha completa de uma Identity (v2)",
            description = "Versão 2: retorna a Identity já com Stats, Sanity, Skills e Passivas em uma única chamada. "
                    + "O Sinner vem resumido ({id, nome}) e as Tags como lista de nomes. Envie o cabeçalho X-API-Version: 2."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK – Ficha completa da Identity"),
            @ApiResponse(responseCode = "400", description = "Bad Request – Id inválido (não numérico) ou versão da API inexistente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Id inválido", value = ErrorExamples.BAD_REQUEST_ID))),
            @ApiResponse(responseCode = "404", description = "Not Found – Nenhuma Identity com o id informado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "Não encontrado", value = ErrorExamples.NOT_FOUND_IDENTITY)))
    })
    @Parameter(in = ParameterIn.HEADER, name = ApiVersionConfig.HEADER, required = true,
            description = "Versão da API. Este documento descreve a versão 2",
            schema = @Schema(type = "string", defaultValue = "2"))
    @GetMapping(path = "/identities/{id}", version = "2")
    public ResponseEntity<EntityModel<IdentityFichaResponse>> buscarFichaCompleta(
            @Parameter(description = "Id da Identity", example = "1")
            @PathVariable Long id) {
        IdentityFichaResponse ficha = fichaService.montar(id);
        EntityModel<IdentityFichaResponse> model = EntityModel.of(ficha,
                linkTo(methodOn(IdentityController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(IdentityController.class).listar(Pageable.unpaged())).withRel("identities"),
                linkTo(methodOn(SinnerController.class).buscarPorId(ficha.sinner().id())).withRel("sinner"));
        return ResponseEntity.ok(model);
    }
}
