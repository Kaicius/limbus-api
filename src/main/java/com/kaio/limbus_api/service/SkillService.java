package com.kaio.limbus_api.service;

import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.entity.Skill;
import com.kaio.limbus_api.enums.Sin;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.IdentityRepository;
import com.kaio.limbus_api.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final IdentityRepository identityRepository;

    public SkillService(SkillRepository skillRepository,
                        IdentityRepository identityRepository){
        this.skillRepository = skillRepository;
        this.identityRepository = identityRepository;
    }

    public Page<Skill> listar(Pageable pageable){
        return skillRepository.findAll(pageable);
    }

    public Page<Skill> buscarPorSin(Sin sin, Pageable pageable){
        return skillRepository.findBySin(sin, pageable);
    }

    public Skill buscarPorId(Long id){
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));
    }

    public Skill criar(Long identityId, Skill skill){
        Identity identity = identityRepository.findById(identityId)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", identityId));

        skill.setIdentity(identity);
        return skillRepository.save(skill);
    }

    public Skill atualizar(Long id, Skill skillAtualizada){
        Skill skill = buscarPorId(id);
        skill.setSlot(skillAtualizada.getSlot());
        skill.setVariante(skillAtualizada.getVariante());
        skill.setSin(skillAtualizada.getSin());
        skill.setNome(skillAtualizada.getNome());
        skill.setQuantidadeCoins(skillAtualizada.getQuantidadeCoins());
        skill.setDescricaoEfeito(skillAtualizada.getDescricaoEfeito());
        return skillRepository.save(skill);
    }

    public void deletar(Long id){
        Skill skill = buscarPorId(id);
        skillRepository.delete(skill);
    }
}
