package com.itskills.hexagonal.avant.repository;

import com.itskills.hexagonal.avant.entity.CommandeEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeRepository extends JpaRepository<CommandeEntity, UUID> {
}
