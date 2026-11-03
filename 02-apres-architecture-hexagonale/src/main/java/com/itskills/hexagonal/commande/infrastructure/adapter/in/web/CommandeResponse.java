package com.itskills.hexagonal.commande.infrastructure.adapter.in.web;

import com.itskills.hexagonal.commande.domain.model.Commande;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO REST de sortie : protège le contrat d'API des évolutions internes du domaine ou de la base SQL.
 */
public record CommandeResponse(
        UUID id,
        String emailClient,
        BigDecimal montantTotal,
        String statut,
        String referencePaiement
) {
    public static CommandeResponse depuisDomaine(Commande commande) {
        return new CommandeResponse(
                commande.id().valeur(),
                commande.emailClient().valeur(),
                commande.montantTotal().valeur(),
                commande.statut().name(),
                commande.referencePaiement()
        );
    }
}
