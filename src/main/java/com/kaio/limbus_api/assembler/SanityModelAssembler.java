package com.kaio.limbus_api.assembler;

import com.kaio.limbus_api.entity.Sanity;
import com.kaio.limbus_api.controller.IdentityController;
import com.kaio.limbus_api.controller.SanityController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Transforma Sanity em EntityModel, acrescentando os links HATEOAS:
 * self, update, delete, a coleção e os recursos relacionados.
 */
@Component
public class SanityModelAssembler implements RepresentationModelAssembler<Sanity, EntityModel<Sanity>> {

    @Override
    public EntityModel<Sanity> toModel(Sanity sanity) {
        return EntityModel.of(sanity,
                linkTo(methodOn(SanityController.class).buscarPorId(sanity.getId())).withSelfRel(),
                linkTo(methodOn(SanityController.class).atualizar(sanity.getId(), null)).withRel("update"),
                linkTo(methodOn(SanityController.class).deletar(sanity.getId())).withRel("delete"),
                linkTo(methodOn(SanityController.class).listar(Pageable.unpaged())).withRel("sanities"),
                linkTo(methodOn(IdentityController.class).buscarPorId(sanity.getIdentity().getId())).withRel("identity"));
    }
}
