package com.kaio.limbus_api.assembler;

import com.kaio.limbus_api.entity.Sinner;
import com.kaio.limbus_api.controller.IdentityController;
import com.kaio.limbus_api.controller.SinnerController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Transforma Sinner em EntityModel, acrescentando os links HATEOAS:
 * self, update, delete, a coleção e os recursos relacionados.
 */
@Component
public class SinnerModelAssembler implements RepresentationModelAssembler<Sinner, EntityModel<Sinner>> {

    @Override
    public EntityModel<Sinner> toModel(Sinner sinner) {
        return EntityModel.of(sinner,
                linkTo(methodOn(SinnerController.class).buscarPorId(sinner.getId())).withSelfRel(),
                linkTo(methodOn(SinnerController.class).atualizar(sinner.getId(), null)).withRel("update"),
                linkTo(methodOn(SinnerController.class).deletar(sinner.getId())).withRel("delete"),
                linkTo(methodOn(SinnerController.class).listar(Pageable.unpaged())).withRel("sinners"),
                linkTo(methodOn(IdentityController.class).buscarPorSinner(sinner.getId(), Pageable.unpaged())).withRel("identities"));
    }
}
