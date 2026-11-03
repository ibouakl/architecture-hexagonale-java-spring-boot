package com.itskills.hexagonal.avant.service;

import com.itskills.hexagonal.avant.entity.CommandeEntity;
import com.itskills.hexagonal.avant.repository.CommandeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ANTI-PATTERN : "God Service" procédural en architecture 3 couches.
 * - Dépend directement de Spring Data JPA (CommandeRepository).
 * - Contient toute la logique métier mélangée aux appels techniques.
 * - Maintient la transaction SQL ouverte pendant l'appel réseau de paiement !
 */
@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;

    public CommandeService(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    @Transactional
    public CommandeEntity creerCommande(String emailClient, BigDecimal montantBrut, boolean clientVip) {
        if (emailClient == null || !emailClient.contains("@")) {
            throw new IllegalArgumentException("Email client invalide");
        }
        if (montantBrut == null || montantBrut.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }

        // Règle métier noyée dans le service technique : remise VIP de 10% au-delà de 100 €
        BigDecimal montantFinal = montantBrut;
        if (clientVip && montantBrut.compareTo(new BigDecimal("100.00")) >= 0) {
            montantFinal = montantBrut.multiply(new BigDecimal("0.90"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        CommandeEntity entity = new CommandeEntity();
        entity.setEmailClient(emailClient);
        entity.setMontantTotal(montantFinal);
        entity.setStatut("CREEE");

        return commandeRepository.save(entity);
    }

    @Transactional
    public CommandeEntity payerCommande(UUID commandeId, String tokenCarte) {
        CommandeEntity commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        if (!"CREEE".equals(commande.getStatut())) {
            throw new IllegalStateException("Seule une commande à l'état CREEE peut être payée");
        }
        if (tokenCarte == null || tokenCarte.isBlank()) {
            throw new IllegalArgumentException("Token de carte bancaire manquant");
        }

        // Simulation d'un appel HTTP externe en plein milieu de la transaction SQL !
        String referenceStripe = "ch_stripe_" + UUID.randomUUID().toString().substring(0, 8);

        commande.setStatut("PAYEE");
        commande.setReferencePaiement(referenceStripe);
        return commandeRepository.save(commande);
    }
}
