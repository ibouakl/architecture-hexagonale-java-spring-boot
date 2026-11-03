package com.itskills.hexagonal.commande.domain.model;

import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;

/**
 * VALUE OBJECT : Ligne de commande immuable.
 */
public record LigneCommande(String codeProduit, int quantite, Montant prixUnitaire) {

    public LigneCommande {
        if (codeProduit == null || codeProduit.isBlank()) {
            throw new RegleMetierException("Le code produit est obligatoire");
        }
        if (quantite <= 0) {
            throw new RegleMetierException("La quantité commandée doit être supérieure à zéro");
        }
        if (prixUnitaire == null || prixUnitaire.valeur().signum() <= 0) {
            throw new RegleMetierException("Le prix unitaire doit être strictement positif");
        }
    }

    public Montant sousTotal() {
        return prixUnitaire.multiplier(quantite);
    }
}
