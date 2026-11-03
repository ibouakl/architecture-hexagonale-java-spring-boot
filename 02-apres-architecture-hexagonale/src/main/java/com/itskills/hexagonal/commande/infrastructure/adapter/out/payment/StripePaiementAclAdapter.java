package com.itskills.hexagonal.commande.infrastructure.adapter.out.payment;

import com.itskills.hexagonal.commande.domain.model.CommandeId;
import com.itskills.hexagonal.commande.domain.model.Montant;
import com.itskills.hexagonal.commande.domain.port.out.PaiementGatewayPort;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * ADAPTATEUR SECONDAIRE + ANTI-CORRUPTION LAYER (ACL - DDD Stratégique) :
 * Empêche le vocabulaire et les types de l'API externe Stripe de contaminer notre Domaine métier.
 */
@Component
public class StripePaiementAclAdapter implements PaiementGatewayPort {

    @Override
    public ResultatPaiement debiterCarte(CommandeId commandeId, Montant montant, String tokenCarte) {
        long centimes = montant.valeur().multiply(BigDecimal.valueOf(100)).longValueExact();

        // Appel simulé au SDK externe Stripe
        StripeChargePayload reponseExterne = appelerApiStripeExterne(centimes, tokenCarte);

        // Traduction ACL : Modèle externe Stripe -> Langage Ubiquitaire du Domaine
        if (reponseExterne.httpStatus() == 200 && reponseExterne.chargeId() != null) {
            return new ResultatPaiement.Succes(reponseExterne.chargeId());
        }
        return new ResultatPaiement.Echec("Code erreur prestataire : " + reponseExterne.failureCode());
    }

    private StripeChargePayload appelerApiStripeExterne(long centimes, String tokenCarte) {
        if (tokenCarte == null || tokenCarte.startsWith("tok_refuse")) {
            return new StripeChargePayload(402, null, centimes, "card_declined_insufficient_funds");
        }
        String chargeId = "ch_stripe_" + UUID.randomUUID().toString().substring(0, 8);
        return new StripeChargePayload(200, chargeId, centimes, null);
    }
}
