package com.kaio.limbus_api.assembler;

import com.kaio.limbus_api.entity.Passive;
import com.kaio.limbus_api.controller.IdentityController;
import com.kaio.limbus_api.controller.PassiveController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Transforma Passive em EntityModel, acrescentando os links HATEOAS:
 * self, update, delete, a coleção e os recursos relacionados.
 */
@Component
public class PassiveModelAssembler implements RepresentationModelAssembler<Passive, EntityModel<Passive>> {

    @Override
    public EntityModel<Passive> toModel(Passive passive) {
        return EntityModel.of(passive,
                linkTo(methodOn(PassiveController.class).buscarPorId(passive.getId())).withSelfRel(),
                linkTo(methodOn(PassiveController.class).atualizar(passive.getId(), null)).withRel("update"),
                linkTo(methodOn(PassiveController.class).deletar(passive.getId())).withRel("delete"),
                linkTo(methodOn(PassiveController.class).listar(Pageable.unpaged())).withRel("passives"),
                linkTo(methodOn(IdentityController.class).buscarPorId(passive.getIdentity().getId())).withRel("identity"));
    }
}
