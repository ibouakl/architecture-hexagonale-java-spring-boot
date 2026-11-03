package com.itskills.hexagonal.commande.domain.model;

import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;

/**
 * VALUE OBJECT : EmailClient immuable et auto-validant via constructeur compact Java.
 * Un objet EmailClient invalide ne peut tout simplement pas exister en mémoire.
 */
public record EmailClient(String valeur) {

    public EmailClient {
        if (valeur == null || valeur.isBlank() || !valeur.contains("@")) {
            throw new RegleMetierException("Format d'email client invalide : " + valeur);
        }
        valeur = valeur.trim().toLowerCase();
    }
}
