package com.kaio.limbus_api.repository;

import com.kaio.limbus_api.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByHash(String hash);
}
