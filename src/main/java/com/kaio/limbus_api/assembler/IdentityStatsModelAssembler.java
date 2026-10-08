package com.kaio.limbus_api.assembler;

import com.kaio.limbus_api.entity.IdentityStats;
import com.kaio.limbus_api.controller.IdentityController;
import com.kaio.limbus_api.controller.IdentityStatsController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Transforma IdentityStats em EntityModel, acrescentando os links HATEOAS:
 * self, update, delete, a coleção e os recursos relacionados.
 */
@Component
public class IdentityStatsModelAssembler implements RepresentationModelAssembler<IdentityStats, EntityModel<IdentityStats>> {

    @Override
    public EntityModel<IdentityStats> toModel(IdentityStats identityStats) {
        return EntityModel.of(identityStats,
                linkTo(methodOn(IdentityStatsController.class).buscarPorId(identityStats.getId())).withSelfRel(),
                linkTo(methodOn(IdentityStatsController.class).atualizar(identityStats.getId(), null)).withRel("update"),
                linkTo(methodOn(IdentityStatsController.class).deletar(identityStats.getId())).withRel("delete"),
                linkTo(methodOn(IdentityStatsController.class).listar(Pageable.unpaged())).withRel("identity-stats"),
                linkTo(methodOn(IdentityController.class).buscarPorId(identityStats.getIdentity().getId())).withRel("identity"));
    }
}
