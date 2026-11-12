package com.kaio.limbus_api.service;

import com.kaio.limbus_api.dto.v2.IdentityFichaResponse;
import com.kaio.limbus_api.entity.Identity;
import com.kaio.limbus_api.entity.Passive;
import com.kaio.limbus_api.entity.Skill;
import com.kaio.limbus_api.exception.ResourceNotFoundException;
import com.kaio.limbus_api.repository.IdentityRepository;
import com.kaio.limbus_api.repository.IdentityStatsRepository;
import com.kaio.limbus_api.repository.PassiveRepository;
import com.kaio.limbus_api.repository.SanityRepository;
import com.kaio.limbus_api.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class IdentityFichaService {

    private final IdentityRepository identityRepository;
    private final IdentityStatsRepository statsRepository;
    private final SanityRepository sanityRepository;
    private final SkillRepository skillRepository;
    private final PassiveRepository passiveRepository;

    public IdentityFichaService(IdentityRepository identityRepository,
                                IdentityStatsRepository statsRepository,
                                SanityRepository sanityRepository,
                                SkillRepository skillRepository,
                                PassiveRepository passiveRepository) {
        this.identityRepository = identityRepository;
        this.statsRepository = statsRepository;
        this.sanityRepository = sanityRepository;
        this.skillRepository = skillRepository;
        this.passiveRepository = passiveRepository;
    }

    @Transactional(readOnly = true)
    public IdentityFichaResponse montar(Long id) {
        Identity identity = identityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", id));

        List<Skill> skills = skillRepository.findByIdentityIdOrderByIdAsc(id).stream()
                .sorted(Comparator.comparing(Skill::getSlot).thenComparing(Skill::getVariante))
                .toList();
        List<Passive> passives = passiveRepository.findByIdentityIdOrderByIdAsc(id);

        return IdentityFichaResponse.de(
                identity,
                statsRepository.findByIdentityId(id).orElse(null),
                sanityRepository.findByIdentityId(id).orElse(null),
                skills,
                passives);
    }
}
