package com.kaio.limbus_api.repository;

import com.kaio.limbus_api.entity.IdentityStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentityStatsRepository extends JpaRepository<IdentityStats, Long> {

    Optional<IdentityStats> findByIdentityId(Long identityId);
}