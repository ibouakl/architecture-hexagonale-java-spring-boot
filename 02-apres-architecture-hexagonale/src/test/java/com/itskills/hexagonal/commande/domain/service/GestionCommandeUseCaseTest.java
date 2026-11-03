package com.itskills.hexagonal.commande.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itskills.hexagonal.commande.domain.event.CommandePayeeEvent;
import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;
import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.model.CommandeId;
import com.itskills.hexagonal.commande.domain.model.EmailClient;
import com.itskills.hexagonal.commande.domain.model.LigneCommande;
import com.itskills.hexagonal.commande.domain.model.Montant;
import com.itskills.hexagonal.commande.domain.model.StatutCommande;
import com.itskills.hexagonal.commande.domain.port.in.PasserCommandeUseCase.PasserCommandeCommand;
import com.itskills.hexagonal.commande.domain.port.in.PayerCommandeUseCase.PayerCommandeCommand;
import com.itskills.hexagonal.commande.domain.port.out.CommandeRepositoryPort;
import com.itskills.hexagonal.commande.domain.port.out.PaiementGatewayPort;
import com.itskills.hexagonal.commande.domain.port.out.PublicationEvenementPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TEST DE USE CASE AVEC FAKES EN MÉMOIRE (Itération 2) :
 * - Teste l'orchestration complète sans base de données SQL, sans Spring et sans Mockito fragile.
 */
class GestionCommandeUseCaseTest {

    private InMemoryCommandeRepositoryFake repositoryFake;
    private List<CommandePayeeEvent> evenementsPublies;
    private GestionCommandeService useCase;

    @BeforeEach
    void initialiser() {
        repositoryFake = new InMemoryCommandeRepositoryFake();
        evenementsPublies = new ArrayList<>();

        PaiementGatewayPort paiementFake = (id, montant, token) ->
                token.startsWith("tok_valide")
                        ? new PaiementGatewayPort.ResultatPaiement.Succes("tx_fake_001")
                        : new PaiementGatewayPort.ResultatPaiement.Echec("Plafond dépassé");

        PublicationEvenementPort publicationFake = evenementsPublies::add;

        useCase = new GestionCommandeService(repositoryFake, paiementFake, publicationFake);
    }

    @Test
    @DisplayName("Devrait passer puis payer une commande complète en mémoire et publier CommandePayeeEvent")
    void devraitPasserEtPayerCommandeAvecSucces() {
        // GIVEN
        PasserCommandeCommand commandeCreation = new PasserCommandeCommand(
                new EmailClient("client.vip@banque.fr"),
                List.of(new LigneCommande("FORMATION-HEXAGONALE", 1, Montant.euros("200.00"))),
                true
        );
        Commande creee = useCase.executer(commandeCreation);

        // WHEN
        Commande payee = useCase.executer(new PayerCommandeCommand(creee.id(), "tok_valide_visa"));

        // THEN
        assertThat(payee.statut()).isEqualTo(StatutCommande.PAYEE);
        assertThat(payee.montantTotal()).isEqualTo(Montant.euros("180.00"));
        assertThat(payee.referencePaiement()).isEqualTo("tx_fake_001");
        assertThat(evenementsPublies).hasSize(1);
    }

    @Test
    @DisplayName("Devrait conserver la commande à l'état CREEE si le paiement est refusé par le port secondaire")
    void devraitConserverEtatCreeeSiPaiementRefuse() {
        // GIVEN
        Commande creee = useCase.executer(new PasserCommandeCommand(
                new EmailClient("client@entreprise.fr"),
                List.of(new LigneCommande("LIVRE-DDD", 1, Montant.euros("45.00"))),
                false
        ));

        // WHEN / THEN
        assertThatThrownBy(() -> useCase.executer(new PayerCommandeCommand(creee.id(), "tok_refuse")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("Plafond dépassé");

        Commande enBase = repositoryFake.trouverParId(creee.id()).orElseThrow();
        assertThat(enBase.statut()).isEqualTo(StatutCommande.CREEE);
        assertThat(evenementsPublies).isEmpty();
    }

    /**
     * FAKE EN MÉMOIRE : implémentation légère du Port Secondaire basée sur une simple Map Java.
     */
    static class InMemoryCommandeRepositoryFake implements CommandeRepositoryPort {
        private final Map<CommandeId, Commande> stockage = new HashMap<>();

        @Override
        public Commande sauvegarder(Commande commande) {
            stockage.put(commande.id(), commande);
            return commande;
        }

        @Override
        public Optional<Commande> trouverParId(CommandeId id) {
            return Optional.ofNullable(stockage.get(id));
        }
    }
}
