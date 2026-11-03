package com.itskills.hexagonal.commande.domain.model;

import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;
import java.util.UUID;

/**
 * VALUE OBJECT : Identifiant typé de Commande (évite le "Primitive Obsession" avec UUID nu).
 */
public record CommandeId(UUID valeur) {

    public CommandeId {
        if (valeur == null) {
            throw new RegleMetierException("L'identifiant de commande ne peut pas être nul");
        }
    }

    public static CommandeId nouveau() {
        return new CommandeId(UUID.randomUUID());
    }
}
