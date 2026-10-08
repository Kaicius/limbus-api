package com.kaio.limbus_api.repository;

import com.kaio.limbus_api.entity.Passive;
import com.kaio.limbus_api.enums.PassivaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassiveRepository extends JpaRepository<Passive, Long> {

    Page<Passive> findByTipo(PassivaType tipo, Pageable pageable);
}