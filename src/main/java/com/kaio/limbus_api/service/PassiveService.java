package com.kaio.limbus_api.service;

import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.entity.Passive;
import com.kaio.limbus_api.enums.PassivaType;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.IdentityRepository;
import com.kaio.limbus_api.repository.PassiveRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PassiveService {

    private final PassiveRepository passiveRepository;
    private final IdentityRepository identityRepository;

    public PassiveService(PassiveRepository passiveRepository,
                          IdentityRepository identityRepository){
        this.passiveRepository = passiveRepository;
        this.identityRepository = identityRepository;
    }

    public Page<Passive> listar(Pageable pageable){
        return passiveRepository.findAll(pageable);
    }

    public Page<Passive> buscarPorTipo(PassivaType tipo, Pageable pageable){
        return passiveRepository.findByTipo(tipo, pageable);
    }

    public Passive buscarPorId(Long id){
        return passiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passive", id));
    }

    public Passive criar(Long identityId, Passive passive){
        Identity identity = identityRepository.findById(identityId)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", identityId));

        passive.setIdentity(identity);
        return passiveRepository.save(passive);
    }

    public Passive atualizar(Long id, Passive passiveAtualizada){
        Passive passive = buscarPorId(id);
        passive.setTipo(passiveAtualizada.getTipo());
        passive.setNome(passiveAtualizada.getNome());
        passive.setDescricao(passiveAtualizada.getDescricao());
        return passiveRepository.save(passive);
    }

    public void deletar(Long id){
        Passive passive = buscarPorId(id);
        passiveRepository.delete(passive);
    }
}