package com.kaio.limbus_api.repository;

import com.kaio.limbus_api.entity.Identity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityRepository extends JpaRepository<Identity, Long> {

    Page<Identity> findBySinnerId(Long sinnerId, Pageable pageable);

    Page<Identity> findByTagsNome(String tagNome, Pageable pageable);
}
