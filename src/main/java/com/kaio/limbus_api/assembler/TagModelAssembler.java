package com.kaio.limbus_api.assembler;

import com.kaio.limbus_api.entity.Tag;
import com.kaio.limbus_api.controller.IdentityController;
import com.kaio.limbus_api.controller.TagController;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Transforma Tag em EntityModel, acrescentando os links HATEOAS:
 * self, update, delete, a coleção e os recursos relacionados.
 */
@Component
public class TagModelAssembler implements RepresentationModelAssembler<Tag, EntityModel<Tag>> {

    @Override
    public EntityModel<Tag> toModel(Tag tag) {
        return EntityModel.of(tag,
                linkTo(methodOn(TagController.class).buscarPorId(tag.getId())).withSelfRel(),
                linkTo(methodOn(TagController.class).atualizar(tag.getId(), null)).withRel("update"),
                linkTo(methodOn(TagController.class).deletar(tag.getId())).withRel("delete"),
                linkTo(methodOn(TagController.class).listar(Pageable.unpaged())).withRel("tags"),
                linkTo(methodOn(IdentityController.class).buscarPorTag(tag.getNome(), Pageable.unpaged())).withRel("identities"));
    }
}
