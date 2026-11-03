package com.itskills.hexagonal.commande.domain.event;

import com.itskills.hexagonal.commande.domain.model.CommandeId;
import com.itskills.hexagonal.commande.domain.model.EmailClient;
import com.itskills.hexagonal.commande.domain.model.Montant;
import java.time.Instant;

/**
 * DOMAIN EVENT (DDD) : Événement métier immuable émis au passé lorsqu'une commande est payée.
 */
public record CommandePayeeEvent(
        CommandeId commandeId,
        EmailClient emailClient,
        Montant montantPaye,
        String referencePaiement,
        Instant datePaiement
) {
}
