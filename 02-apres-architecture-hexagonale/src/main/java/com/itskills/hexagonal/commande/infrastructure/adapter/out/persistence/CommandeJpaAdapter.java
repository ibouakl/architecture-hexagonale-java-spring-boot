package com.itskills.hexagonal.commande.infrastructure.adapter.out.persistence;

import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.model.CommandeId;
import com.itskills.hexagonal.commande.domain.port.out.CommandeRepositoryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ADAPTATEUR SECONDAIRE (Driven Adapter) JPA :
 * - Implémente le Port Secondaire CommandeRepositoryPort défini par le Domaine (Inversion de Dépendance).
 * - Gère la transaction SQL (@Transactional) au niveau de l'infrastructure.
 */
@Component
public class CommandeJpaAdapter implements CommandeRepositoryPort {

    private final SpringDataCommandeRepository springDataRepository;
    private final CommandeJpaMapper mapper;

    public CommandeJpaAdapter(
            SpringDataCommandeRepository springDataRepository,
            CommandeJpaMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Commande sauvegarder(Commande commande) {
        CommandeJpaEntity entity = mapper.versJpaEntity(commande);
        CommandeJpaEntity sauvegardee = springDataRepository.save(entity);
        return mapper.versDomaine(sauvegardee);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Commande> trouverParId(CommandeId id) {
        return springDataRepository.findById(id.valeur())
                .map(mapper::versDomaine);
    }
}
