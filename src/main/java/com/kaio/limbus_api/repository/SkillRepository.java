package com.kaio.limbus_api.repository;

import com.kaio.limbus_api.entity.Skill;
import com.kaio.limbus_api.enums.Sin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Page<Skill> findBySin(Sin sin, Pageable pageable);
    Page<Skill> findByIdentityId(Long identityId, Pageable pageable);
    List<Skill> findByIdentityIdOrderByIdAsc(Long identityId);
}
