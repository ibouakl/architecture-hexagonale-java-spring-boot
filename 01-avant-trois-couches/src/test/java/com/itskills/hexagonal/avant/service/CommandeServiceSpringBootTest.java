package com.itskills.hexagonal.avant.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.itskills.hexagonal.avant.entity.CommandeEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * SYMPTÔME : Pour vérifier une simple remise de 10%, on démarre @SpringBootTest,
 * le pool HikariCP, Hibernate DDL et H2 en mémoire (plusieurs secondes au démarrage).
 */
@SpringBootTest
class CommandeServiceSpringBootTest {

    @Autowired
    private CommandeService commandeService;

    @Test
    @DisplayName("Devrait appliquer 10% de remise pour un client VIP au-delà de 100 EUR (Test lourd @SpringBootTest)")
    void devraitAppliquerRemiseVipAvecContexteSpringComplet() {
        // GIVEN
        String email = "alice@entreprise.fr";
        BigDecimal montantBrut = new BigDecimal("200.00");

        // WHEN
        CommandeEntity sauvegardee = commandeService.creerCommande(email, montantBrut, true);

        // THEN
        assertThat(sauvegardee.getId()).isNotNull();
        assertThat(sauvegardee.getMontantTotal()).isEqualByComparingTo("180.00");
        assertThat(sauvegardee.getStatut()).isEqualTo("CREEE");
    }
}
