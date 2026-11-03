package com.itskills.hexagonal.commande.infrastructure.adapter.out.payment;

/**
 * MODÈLE EXTERNE TIERS (Prestataire de paiement Stripe) :
 * Utilise des conventions étrangères à notre domaine (montant en centimes, codes numériques, anglais).
 */
public record StripeChargePayload(
        int httpStatus,
        String chargeId,
        long amountInCents,
        String failureCode
) {
}
