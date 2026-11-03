package com.itskills.hexagonal.commande.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.model.EmailClient;
import com.itskills.hexagonal.commande.domain.model.LigneCommande;
import com.itskills.hexagonal.commande.domain.model.Montant;
import com.itskills.hexagonal.commande.domain.model.StatutCommande;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * TEST D'ADAPTATEUR SECONDAIRE JPA (@DataJpaTest — Itération 3) :
 * Vérifie uniquement la traduction Domaine <-> Entité JPA et le mapping SQL,
 * indépendamment des règles métier déjà testées en 5 ms.
 */
@DataJpaTest
@Import({CommandeJpaAdapter.class, CommandeJpaMapper.class})
class CommandeJpaAdapterIntegrationTest {

    @Autowired
    private CommandeJpaAdapter jpaAdapter;

    @Test
    @DisplayName("Devrait sauvegarder et reconstituer fidèlement l'agrégat Commande via CommandeJpaEntity")
    void devraitSauvegarderEtReconstituerAgregatCommande() {
        // GIVEN
        Commande commandeDomaine = Commande.creer(
                new EmailClient("architecte@entreprise.fr"),
                List.of(new LigneCommande("PACK-ARCHITECTURE", 2, Montant.euros("150.00"))),
                true
        );
        commandeDomaine.marquerCommePayee("ch_stripe_test99");

        // WHEN
        jpaAdapter.sauvegarder(commandeDomaine);
        Commande reconstituee = jpaAdapter.trouverParId(commandeDomaine.id()).orElseThrow();

        // THEN
        assertThat(reconstituee.id()).isEqualTo(commandeDomaine.id());
        assertThat(reconstituee.emailClient()).isEqualTo(new EmailClient("architecte@entreprise.fr"));
        assertThat(reconstituee.montantTotal()).isEqualTo(Montant.euros("270.00"));
        assertThat(reconstituee.statut()).isEqualTo(StatutCommande.PAYEE);
        assertThat(reconstituee.referencePaiement()).isEqualTo("ch_stripe_test99");
        assertThat(reconstituee.lignes()).hasSize(1);
    }
}
