package com.itskills.hexagonal.avant.controller;

import com.itskills.hexagonal.avant.entity.CommandeEntity;
import com.itskills.hexagonal.avant.service.CommandeService;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ANTI-PATTERN : Le contrôleur expose directement l'entité JPA (@Entity) dans la réponse JSON.
 * Le moindre renommage d'une colonne SQL casse le contrat d'API REST !
 */
@RestController
@RequestMapping("/api/v1/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    public CommandeEntity creer(
            @RequestParam String email,
            @RequestParam BigDecimal montant,
            @RequestParam(defaultValue = "false") boolean vip) {
        return commandeService.creerCommande(email, montant, vip);
    }

    @PostMapping("/{id}/paiement")
    public CommandeEntity payer(@PathVariable UUID id, @RequestParam String tokenCarte) {
        return commandeService.payerCommande(id, tokenCarte);
    }
}
