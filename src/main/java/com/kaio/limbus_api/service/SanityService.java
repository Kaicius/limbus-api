package com.kaio.limbus_api.service;

import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.entity.Sanity;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.IdentityRepository;
import com.kaio.limbus_api.repository.SanityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SanityService {

    private final SanityRepository sanityRepository;
    private final IdentityRepository identityRepository;

    public SanityService(SanityRepository sanityRepository,
                         IdentityRepository identityRepository){
        this.sanityRepository = sanityRepository;
        this.identityRepository = identityRepository;
    }

    public Page<Sanity> listar(Pageable pageable){
        return sanityRepository.findAll(pageable);
    }

    public Sanity buscarPorId(Long id){
        return sanityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sanity", id));
    }

    public Sanity buscarPorIdentity(Long identityId){
        return sanityRepository.findByIdentityId(identityId)
                .orElseThrow(() -> new ResourceNotFoundException("Sanity da Identity", identityId));
    }

    public Sanity criar(Long identityId, Sanity sanity){
        Identity identity = identityRepository.findById(identityId)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", identityId));

        sanity.setIdentity(identity);
        return sanityRepository.save(sanity);
    }

    public Sanity atualizar(Long id, Sanity sanityAtualizada){
        Sanity sanity = buscarPorId(id);
        sanity.setPanicType(sanityAtualizada.getPanicType());
        sanity.setIncreasingFactors(sanityAtualizada.getIncreasingFactors());
        sanity.setDecreasingFactors(sanityAtualizada.getDecreasingFactors());
        return sanityRepository.save(sanity);
    }

    public void deletar(Long id){
        Sanity sanity = buscarPorId(id);
        sanityRepository.delete(sanity);
    }
}