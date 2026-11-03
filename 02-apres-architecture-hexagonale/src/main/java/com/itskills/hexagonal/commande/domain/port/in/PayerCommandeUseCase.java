package com.itskills.hexagonal.commande.domain.port.in;

import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.model.CommandeId;

/**
 * PORT PRIMAIRE (Driving / API) : Exprime l'intention métier "Payer une commande".
 */
public interface PayerCommandeUseCase {

    Commande executer(PayerCommandeCommand command);

    record PayerCommandeCommand(
            CommandeId commandeId,
            String tokenCarteBancaire
    ) {
    }
}
