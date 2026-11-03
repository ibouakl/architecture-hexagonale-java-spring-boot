package com.itskills.hexagonal.commande.domain.port.out;

import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.model.CommandeId;
import java.util.Optional;

/**
 * PORT SECONDAIRE (Driven / SPI) : Contrat de persistance dicté par le Domaine.
 * Remarquez les verbes métier en français et l'absence totale de JpaRepository ou Pageable.
 */
public interface CommandeRepositoryPort {

    Commande sauvegarder(Commande commande);

    Optional<Commande> trouverParId(CommandeId id);
}
