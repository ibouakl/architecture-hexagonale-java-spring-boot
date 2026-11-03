package com.itskills.hexagonal.commande.infrastructure.adapter.out.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCommandeRepository extends JpaRepository<CommandeJpaEntity, UUID> {
}
