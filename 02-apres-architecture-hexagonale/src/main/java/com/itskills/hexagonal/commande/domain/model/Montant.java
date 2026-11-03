package com.itskills.hexagonal.commande.domain.model;

import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * VALUE OBJECT : Montant monétaire immuable (échelle fixe de 2 décimales, toujours >= 0).
 * Encapsule les calculs financiers au lieu de disperser des BigDecimal dans les services.
 */
public record Montant(BigDecimal valeur) {

    public static final Montant ZERO = new Montant(BigDecimal.ZERO);
    public static final Montant SEUIL_REMISE_VIP = Montant.euros("100.00");

    public Montant {
        if (valeur == null) {
            throw new RegleMetierException("Le montant ne peut pas être nul");
        }
        valeur = valeur.setScale(2, RoundingMode.HALF_UP);
        if (valeur.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegleMetierException("Le montant ne peut pas être négatif : " + valeur);
        }
    }

    public static Montant euros(String montantTexte) {
        return new Montant(new BigDecimal(montantTexte));
    }

    public Montant additionner(Montant autre) {
        return new Montant(this.valeur.add(autre.valeur));
    }

    public Montant multiplier(int quantite) {
        if (quantite <= 0) {
            throw new RegleMetierException("La quantité doit être strictement positive");
        }
        return new Montant(this.valeur.multiply(BigDecimal.valueOf(quantite)));
    }

    public Montant appliquerRemisePourcentage(int pourcentage) {
        if (pourcentage < 0 || pourcentage > 100) {
            throw new RegleMetierException("Pourcentage de remise invalide : " + pourcentage);
        }
        BigDecimal coefficient = BigDecimal.valueOf(100 - pourcentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return new Montant(this.valeur.multiply(coefficient));
    }

    public boolean estSuperieurOuEgalA(Montant seuil) {
        return this.valeur.compareTo(seuil.valeur) >= 0;
    }
}
