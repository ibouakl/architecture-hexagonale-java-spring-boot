package com.itskills.hexagonal.commande.domain.exception;

import com.itskills.hexagonal.commande.domain.model.CommandeId;

public class CommandeIntrouvableException extends RegleMetierException {

    public CommandeIntrouvableException(CommandeId id) {
        super("Aucune commande trouvée pour l'identifiant : " + id.valeur());
    }
}
