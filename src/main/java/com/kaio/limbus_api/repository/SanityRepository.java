package com.kaio.limbus_api.repository;

import com.kaio.limbus_api.entity.Sanity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SanityRepository extends JpaRepository<Sanity, Long> {

    Optional<Sanity> findByIdentityId(Long identityId);
}