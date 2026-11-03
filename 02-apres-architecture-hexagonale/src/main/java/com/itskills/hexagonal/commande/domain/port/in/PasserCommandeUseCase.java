package com.itskills.hexagonal.commande.domain.port.in;

import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.model.EmailClient;
import com.itskills.hexagonal.commande.domain.model.LigneCommande;
import java.util.List;

/**
 * PORT PRIMAIRE (Driving / API) : Exprime l'intention métier "Passer une commande".
 * Appelé par les adaptateurs primaires (Contrôleur REST, CLI, Consumer Kafka...).
 */
public interface PasserCommandeUseCase {

    Commande executer(PasserCommandeCommand command);

    record PasserCommandeCommand(
            EmailClient emailClient,
            List<LigneCommande> lignes,
            boolean clientVip
    ) {
    }
}
