package com.itskills.hexagonal.commande.infrastructure.adapter.in.web;

import com.itskills.hexagonal.commande.domain.model.Commande;
import com.itskills.hexagonal.commande.domain.model.CommandeId;
import com.itskills.hexagonal.commande.domain.port.in.PasserCommandeUseCase;
import com.itskills.hexagonal.commande.domain.port.in.PayerCommandeUseCase;
import com.itskills.hexagonal.commande.domain.port.in.PayerCommandeUseCase.PayerCommandeCommand;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * ADAPTATEUR PRIMAIRE (Driving Adapter) REST :
 * - Dépend uniquement des interfaces des Ports Primaires (Use Cases).
 * - Ne contient aucune règle métier : convertit HTTP <-> Domaine.
 */
@RestController
@RequestMapping("/api/v2/commandes")
public class CommandeRestController {

    private final PasserCommandeUseCase passerCommandeUseCase;
    private final PayerCommandeUseCase payerCommandeUseCase;

    public CommandeRestController(
            PasserCommandeUseCase passerCommandeUseCase,
            PayerCommandeUseCase payerCommandeUseCase) {
        this.passerCommandeUseCase = passerCommandeUseCase;
        this.payerCommandeUseCase = payerCommandeUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeResponse passerCommande(@RequestBody PasserCommandeRequest request) {
        Commande commande = passerCommandeUseCase.executer(request.versCommande());
        return CommandeResponse.depuisDomaine(commande);
    }

    @PostMapping("/{id}/paiement")
    public CommandeResponse payerCommande(
            @PathVariable UUID id,
            @RequestParam String tokenCarte) {
        Commande commande = payerCommandeUseCase.executer(
                new PayerCommandeCommand(new CommandeId(id), tokenCarte)
        );
        return CommandeResponse.depuisDomaine(commande);
    }
}
