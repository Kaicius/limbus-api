package com.kaio.limbus_api.assembler;

import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.controller.IdentityController;
import com.kaio.limbus_api.controller.IdentityStatsController;
import com.kaio.limbus_api.controller.SanityController;
import com.kaio.limbus_api.controller.SinnerController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Transforma Identity em EntityModel, acrescentando os links HATEOAS:
 * self, update, delete, a coleção e os recursos relacionados.
 */
@Component
public class IdentityModelAssembler implements RepresentationModelAssembler<Identity, EntityModel<Identity>> {

    @Override
    public EntityModel<Identity> toModel(Identity identity) {
        return EntityModel.of(identity,
                linkTo(methodOn(IdentityController.class).buscarPorId(identity.getId())).withSelfRel(),
                linkTo(methodOn(IdentityController.class).atualizar(identity.getId(), null)).withRel("update"),
                linkTo(methodOn(IdentityController.class).deletar(identity.getId())).withRel("delete"),
                linkTo(methodOn(IdentityController.class).listar(Pageable.unpaged())).withRel("identities"),
                linkTo(methodOn(SinnerController.class).buscarPorId(identity.getSinner().getId())).withRel("sinner"),
                linkTo(methodOn(IdentityStatsController.class).buscarPorIdentity(identity.getId())).withRel("stats"),
                linkTo(methodOn(SanityController.class).buscarPorIdentity(identity.getId())).withRel("sanity"));
    }
}
