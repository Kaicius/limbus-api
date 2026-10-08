package com.kaio.limbus_api.repository;

import com.kaio.limbus_api.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Page<Tag> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}