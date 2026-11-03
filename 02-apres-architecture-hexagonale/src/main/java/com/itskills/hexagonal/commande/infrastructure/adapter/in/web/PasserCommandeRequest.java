package com.itskills.hexagonal.commande.infrastructure.adapter.in.web;

import com.itskills.hexagonal.commande.domain.model.EmailClient;
import com.itskills.hexagonal.commande.domain.model.LigneCommande;
import com.itskills.hexagonal.commande.domain.model.Montant;
import com.itskills.hexagonal.commande.domain.port.in.PasserCommandeUseCase.PasserCommandeCommand;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO REST d'entrée : appartient exclusivement à l'adaptateur Web.
 * Traduit le payload JSON en Commande du Port Primaire.
 */
public record PasserCommandeRequest(
        String emailClient,
        List<LigneRequest> lignes,
        boolean clientVip
) {
    public record LigneRequest(String codeProduit, int quantite, BigDecimal prixUnitaire) {
        public LigneCommande versDomaine() {
            return new LigneCommande(codeProduit, quantite, new Montant(prixUnitaire));
        }
    }

    public PasserCommandeCommand versCommande() {
        List<LigneCommande> lignesDomaine = (lignes == null)
                ? List.of()
                : lignes.stream().map(LigneRequest::versDomaine).toList();
        return new PasserCommandeCommand(new EmailClient(emailClient), lignesDomaine, clientVip);
    }
}
