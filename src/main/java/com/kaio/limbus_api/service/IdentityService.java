package com.kaio.limbus_api.service;

import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.entity.Sinner;
import com.kaio.limbus_api.entity.Tag;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.IdentityRepository;
import com.kaio.limbus_api.repository.SinnerRepository;
import com.kaio.limbus_api.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class IdentityService {

    private final IdentityRepository identityRepository;
    private final SinnerRepository sinnerRepository;
    private final TagRepository tagRepository;

    public IdentityService(IdentityRepository identityRepository,
                           SinnerRepository sinnerRepository,
                           TagRepository tagRepository) {
        this.identityRepository = identityRepository;
        this.sinnerRepository = sinnerRepository;
        this.tagRepository = tagRepository;
    }

    public Page<Identity> listar(Pageable pageable){
        return identityRepository.findAll(pageable);
    }

    public Page<Identity> buscarPorSinner(Long sinnerId, Pageable pageable){
        if (!sinnerRepository.existsById(sinnerId)) {
            throw new ResourceNotFoundException("Sinner", sinnerId);
        }
        return identityRepository.findBySinnerId(sinnerId, pageable);
    }

    public Page<Identity> buscarPorTag(String tagNome, Pageable pageable){
        return identityRepository.findByTagsNome(tagNome, pageable);
    }

    public Identity buscarPorId(Long id) {
        return identityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", id));
    }

    public Identity criar(Long sinnerId, Identity identity, Set<Long> tagIds){
        Sinner sinner = sinnerRepository.findById(sinnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinner", sinnerId));

        identity.setSinner(sinner);
        identity.setTags(buscarTags(tagIds));

        return identityRepository.save(identity);
    }

    public Identity atualizar(Long id, Identity identityAtualizada, Set<Long> tagIds){
        Identity identity = buscarPorId(id);
        identity.setNome(identityAtualizada.getNome());
        identity.setUptie(identityAtualizada.getUptie());
        identity.setRarity(identityAtualizada.getRarity());
        identity.setTags(buscarTags(tagIds));

        return identityRepository.save(identity);
    }

    public void deletar(Long id){
        Identity identity = buscarPorId(id);
        identityRepository.delete(identity);
    }

    private Set<Tag> buscarTags(Set<Long> tagIds) {
        Set<Tag> tags = new HashSet<>();
        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tag", tagId));
            tags.add(tag);
        }
        return tags;
    }
}
