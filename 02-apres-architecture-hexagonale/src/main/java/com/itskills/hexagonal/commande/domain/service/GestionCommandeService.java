package com.itskills.hexagonal.commande.domain.service;

import com.itskills.hexagonal.commande.domain.event.CommandePayeeEvent;
import com.itskills.hexagonal.commande.domain.exception.CommandeIntrouvableException;
import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;
import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.port.in.PasserCommandeUseCase;
import com.itskills.hexagonal.commande.domain.port.in.PayerCommandeUseCase;
import com.itskills.hexagonal.commande.domain.port.out.CommandeRepositoryPort;
import com.itskills.hexagonal.commande.domain.port.out.PaiementGatewayPort;
import com.itskills.hexagonal.commande.domain.port.out.PaiementGatewayPort.ResultatPaiement;
import com.itskills.hexagonal.commande.domain.port.out.PublicationEvenementPort;

/**
 * ORCHESTRATEUR DE CAS D'USAGE (Use Case Service) :
 * - 100 % Java pur (zéro @Service, zéro @Transactional, zéro import Spring ou JPA).
 * - Coordonne les objets du domaine et les ports secondaires.
 */
public class GestionCommandeService implements PasserCommandeUseCase, PayerCommandeUseCase {

    private final CommandeRepositoryPort commandeRepository;
    private final PaiementGatewayPort paiementGateway;
    private final PublicationEvenementPort publicationEvenement;

    public GestionCommandeService(
            CommandeRepositoryPort commandeRepository,
            PaiementGatewayPort paiementGateway,
            PublicationEvenementPort publicationEvenement) {
        this.commandeRepository = commandeRepository;
        this.paiementGateway = paiementGateway;
        this.publicationEvenement = publicationEvenement;
    }

    @Override
    public Commande executer(PasserCommandeCommand command) {
        Commande nouvelleCommande = Commande.creer(
                command.emailClient(),
                command.lignes(),
                command.clientVip()
        );
        return commandeRepository.sauvegarder(nouvelleCommande);
    }

    @Override
    public Commande executer(PayerCommandeCommand command) {
        Commande commande = commandeRepository.trouverParId(command.commandeId())
                .orElseThrow(() -> new CommandeIntrouvableException(command.commandeId()));

        ResultatPaiement resultat = paiementGateway.debiterCarte(
                commande.id(),
                commande.montantTotal(),
                command.tokenCarteBancaire()
        );

        switch (resultat) {
            case ResultatPaiement.Succes(String referenceTransaction) -> {
                commande.marquerCommePayee(referenceTransaction);
                Commande sauvegardee = commandeRepository.sauvegarder(commande);
                for (CommandePayeeEvent evenement : commande.consommerEvenements()) {
                    publicationEvenement.publier(evenement);
                }
                return sauvegardee;
            }
            case ResultatPaiement.Echec(String motifRefus) ->
                    throw new RegleMetierException("Paiement refusé : " + motifRefus);
        }
    }
}
