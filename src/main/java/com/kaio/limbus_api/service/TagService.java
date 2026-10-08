package com.kaio.limbus_api.service;

import com.kaio.limbus_api.entity.Tag;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public Page<Tag> listar(Pageable pageable){
        return tagRepository.findAll(pageable);
    }

    public Page<Tag> buscarPorNome(String nome, Pageable pageable){
        return tagRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Tag buscarPorId(Long id){
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }

    public Tag criar(Tag tag){
        return tagRepository.save(tag);
    }

    public Tag atualizar(Long id, Tag tagAtualizada){
        Tag tag = buscarPorId(id);
        tag.setNome(tagAtualizada.getNome());
        return tagRepository.save(tag);
    }

    public void deletar(Long id){
        Tag tag = buscarPorId(id);
        tagRepository.delete(tag);
    }
}
