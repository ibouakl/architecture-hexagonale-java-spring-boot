package com.itskills.hexagonal.commande.infrastructure.adapter.out.persistence;

import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.model.CommandeId;
import com.itskills.hexagonal.commande.domain.model.EmailClient;
import com.itskills.hexagonal.commande.domain.model.LigneCommande;
import com.itskills.hexagonal.commande.domain.model.Montant;
import com.itskills.hexagonal.commande.domain.model.StatutCommande;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * MAPPER DE PERSISTANCE : Traduit l'agrégat du Domaine vers l'entité JPA et inversement.
 */
@Component
public class CommandeJpaMapper {

    public CommandeJpaEntity versJpaEntity(Commande commande) {
        List<LigneCommandeJpaEmbeddable> lignesJpa = commande.lignes().stream()
                .map(l -> new LigneCommandeJpaEmbeddable(
                        l.codeProduit(),
                        l.quantite(),
                        l.prixUnitaire().valeur()))
                .toList();

        return new CommandeJpaEntity(
                commande.id().valeur(),
                commande.emailClient().valeur(),
                lignesJpa,
                commande.montantTotal().valeur(),
                commande.statut().name(),
                commande.referencePaiement()
        );
    }

    public Commande versDomaine(CommandeJpaEntity entity) {
        List<LigneCommande> lignesDomaine = entity.getLignes().stream()
                .map(l -> new LigneCommande(
                        l.getCodeProduit(),
                        l.getQuantite(),
                        new Montant(l.getPrixUnitaire())))
                .toList();

        return Commande.reconstituer(
                new CommandeId(entity.getId()),
                new EmailClient(entity.getEmailClient()),
                lignesDomaine,
                new Montant(entity.getMontantTotal()),
                StatutCommande.valueOf(entity.getStatut()),
                entity.getReferencePaiement()
        );
    }
}
