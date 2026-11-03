package com.itskills.hexagonal.commande.domain.port.out;

import com.itskills.hexagonal.commande.domain.model.CommandeId;
import com.itskills.hexagonal.commande.domain.model.Montant;

/**
 * PORT SECONDAIRE (Driven / SPI) : Contrat métier de paiement externe.
 * Utilise une interface scellée (sealed interface) Java moderne pour modéliser le résultat.
 */
public interface PaiementGatewayPort {

    ResultatPaiement debiterCarte(CommandeId commandeId, Montant montant, String tokenCarte);

    sealed interface ResultatPaiement permits ResultatPaiement.Succes, ResultatPaiement.Echec {
        record Succes(String referenceTransaction) implements ResultatPaiement {}
        record Echec(String motifRefus) implements ResultatPaiement {}
    }
}
