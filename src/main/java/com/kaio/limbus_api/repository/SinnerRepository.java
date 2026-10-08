package com.kaio.limbus_api.repository;

import com.kaio.limbus_api.entity.Sinner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinnerRepository extends JpaRepository<Sinner, Long> {

    Page<Sinner> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}