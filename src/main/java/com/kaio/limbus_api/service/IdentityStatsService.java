package com.kaio.limbus_api.service;

import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.entity.IdentityStats;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.IdentityRepository;
import com.kaio.limbus_api.repository.IdentityStatsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class IdentityStatsService {

    private final IdentityStatsRepository identityStatsRepository;
    private final IdentityRepository identityRepository;

    public IdentityStatsService(IdentityStatsRepository identityStatsRepository,
                                IdentityRepository identityRepository) {
        this.identityStatsRepository = identityStatsRepository;
        this.identityRepository = identityRepository;
    }

    public Page<IdentityStats> listar(Pageable pageable) {
        return identityStatsRepository.findAll(pageable);
    }


    public IdentityStats buscarPorId(Long id) {
        return identityStatsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IdentityStats", id));
    }

    public IdentityStats buscarPorIdentity(Long identityId) {
        return identityStatsRepository.findByIdentityId(identityId)
                .orElseThrow(() -> new ResourceNotFoundException("IdentityStats da Identity", identityId));
    }

    public IdentityStats criar(Long identityId, IdentityStats stats) {
        Identity identity = identityRepository.findById(identityId)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", identityId));

        stats.setIdentity(identity);
        return identityStatsRepository.save(stats);
    }

    public IdentityStats atualizar(Long id, IdentityStats statsAtualizado) {
        IdentityStats stats = buscarPorId(id);
        stats.setHp(statsAtualizado.getHp());
        stats.setSpeed(statsAtualizado.getSpeed());
        stats.setDefense(statsAtualizado.getDefense());
        stats.setStaggerThreshold(statsAtualizado.getStaggerThreshold());
        stats.setResistanceSlash(statsAtualizado.getResistanceSlash());
        stats.setResistancePierce(statsAtualizado.getResistancePierce());
        stats.setResistanceBlunt(statsAtualizado.getResistanceBlunt());

        return identityStatsRepository.save(stats);
    }

    public void deletar(Long id) {
        IdentityStats stats = buscarPorId(id);
        identityStatsRepository.delete(stats);
    }
}
