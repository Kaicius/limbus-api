package com.kaio.limbus_api.assembler;

import com.kaio.limbus_api.entity.Skill;
import com.kaio.limbus_api.controller.IdentityController;
import com.kaio.limbus_api.controller.SkillController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Transforma Skill em EntityModel, acrescentando os links HATEOAS:
 * self, update, delete, a coleção e os recursos relacionados.
 */
@Component
public class SkillModelAssembler implements RepresentationModelAssembler<Skill, EntityModel<Skill>> {

    @Override
    public EntityModel<Skill> toModel(Skill skill) {
        return EntityModel.of(skill,
                linkTo(methodOn(SkillController.class).buscarPorId(skill.getId())).withSelfRel(),
                linkTo(methodOn(SkillController.class).atualizar(skill.getId(), null)).withRel("update"),
                linkTo(methodOn(SkillController.class).deletar(skill.getId())).withRel("delete"),
                linkTo(methodOn(SkillController.class).listar(Pageable.unpaged())).withRel("skills"),
                linkTo(methodOn(IdentityController.class).buscarPorId(skill.getIdentity().getId())).withRel("identity"));
    }
}
