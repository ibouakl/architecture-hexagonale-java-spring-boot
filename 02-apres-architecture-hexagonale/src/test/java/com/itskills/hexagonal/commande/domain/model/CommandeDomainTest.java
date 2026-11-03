package com.itskills.hexagonal.commande.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TESTS UNITAIRES DU DOMAINE PUR (Itération 1) :
 * - Zéro Spring, zéro base de données, zéro Mock.
 * - Exécution en moins de 10 millisecondes !
 */
class CommandeDomainTest {

    @Test
    @DisplayName("Devrait appliquer automatiquement 10 % de remise pour un client VIP à partir de 100 EUR")
    void devraitAppliquerRemiseVipAuDelaDeCentEuros() {
        // GIVEN
        EmailClient email = new EmailClient("alice@entreprise.fr");
        List<LigneCommande> lignes = List.of(
                new LigneCommande("CLAVIER-MECA", 2, Montant.euros("100.00"))
        );

        // WHEN
        Commande commande = Commande.creer(email, lignes, true);

        // THEN
        assertThat(commande.montantTotal()).isEqualTo(Montant.euros("180.00"));
        assertThat(commande.statut()).isEqualTo(StatutCommande.CREEE);
    }

    @Test
    @DisplayName("Devrait refuser la création d'un Value Object Montant négatif")
    void devraitRefuserMontantNegatif() {
        // GIVEN / WHEN / THEN
        assertThatThrownBy(() -> Montant.euros("-15.00"))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("négatif");
    }

    @Test
    @DisplayName("Devrait interdire le paiement d'une commande déjà payée (protection de l'invariant par l'Aggregate Root)")
    void devraitInterdireDoublePaiement() {
        // GIVEN
        Commande commande = Commande.creer(
                new EmailClient("bob@entreprise.fr"),
                List.of(new LigneCommande("SOURIS-PRO", 1, Montant.euros("50.00"))),
                false
        );
        commande.marquerCommePayee("ch_stripe_12345678");

        // WHEN / THEN
        assertThatThrownBy(() -> commande.marquerCommePayee("ch_stripe_99999999"))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("Impossible de payer une commande au statut : PAYEE");
    }

    @Test
    @DisplayName("Devrait émettre un événement métier CommandePayeeEvent lors du paiement")
    void devraitEmettreEvenementDomaineLorsDuPaiement() {
        // GIVEN
        Commande commande = Commande.creer(
                new EmailClient("claire@entreprise.fr"),
                List.of(new LigneCommande("ECRAN-4K", 1, Montant.euros("350.00"))),
                false
        );

        // WHEN
        commande.marquerCommePayee("ch_stripe_abcdef01");

        // THEN
        assertThat(commande.consommerEvenements())
                .hasSize(1)
                .first()
                .satisfies(event -> {
                    assertThat(event.commandeId()).isEqualTo(commande.id());
                    assertThat(event.montantPaye()).isEqualTo(Montant.euros("350.00"));
                    assertThat(event.referencePaiement()).isEqualTo("ch_stripe_abcdef01");
                });
    }
}
