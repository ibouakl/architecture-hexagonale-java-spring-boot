package com.itskills.hexagonal.commande.domain.model;

import com.itskills.hexagonal.commande.domain.event.CommandePayeeEvent;
import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * AGGREGATE ROOT (DDD Tactique) : Entité racine du domaine Commande.
 * - Zéro annotation Spring ou JPA/Hibernate (100 % Java pur).
 * - Zéro setter public : toute transition passe par un verbe métier qui protège les invariants.
 */
public class Commande {

    private static final int POURCENTAGE_REMISE_VIP = 10;

    private final CommandeId id;
    private final EmailClient emailClient;
    private final List<LigneCommande> lignes;
    private final Montant montantTotal;
    private StatutCommande statut;
    private String referencePaiement;
    private final List<CommandePayeeEvent> evenementsDomaine = new ArrayList<>();

    private Commande(
            CommandeId id,
            EmailClient emailClient,
            List<LigneCommande> lignes,
            Montant montantTotal,
            StatutCommande statut,
            String referencePaiement) {
        this.id = id;
        this.emailClient = emailClient;
        this.lignes = List.copyOf(lignes);
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.referencePaiement = referencePaiement;
    }

    /**
     * Fabrique métier : garantit tous les invariants dès la création de l'agrégat.
     */
    public static Commande creer(EmailClient emailClient, List<LigneCommande> lignes, boolean clientVip) {
        if (emailClient == null) {
            throw new RegleMetierException("L'email client est obligatoire");
        }
        if (lignes == null || lignes.isEmpty()) {
            throw new RegleMetierException("Une commande doit contenir au moins une ligne");
        }

        Montant totalBrut = lignes.stream()
                .map(LigneCommande::sousTotal)
                .reduce(Montant.ZERO, Montant::additionner);

        // Règle métier encapsulée dans l'agrégat : 10 % de remise VIP à partir de 100,00 EUR
        Montant totalFinal = (clientVip && totalBrut.estSuperieurOuEgalA(Montant.SEUIL_REMISE_VIP))
                ? totalBrut.appliquerRemisePourcentage(POURCENTAGE_REMISE_VIP)
                : totalBrut;

        return new Commande(
                CommandeId.nouveau(),
                emailClient,
                lignes,
                totalFinal,
                StatutCommande.CREEE,
                null
        );
    }

    /**
     * Fabrique de reconstitution utilisée uniquement par l'adaptateur de persistance (Mapper JPA).
     */
    public static Commande reconstituer(
            CommandeId id,
            EmailClient emailClient,
            List<LigneCommande> lignes,
            Montant montantTotal,
            StatutCommande statut,
            String referencePaiement) {
        return new Commande(id, emailClient, lignes, montantTotal, statut, referencePaiement);
    }

    /**
     * Comportement métier : valide la transition d'état et enregistre un Domain Event.
     */
    public void marquerCommePayee(String referenceTransaction) {
        if (this.statut != StatutCommande.CREEE) {
            throw new RegleMetierException(
                    "Impossible de payer une commande au statut : " + this.statut);
        }
        if (referenceTransaction == null || referenceTransaction.isBlank()) {
            throw new RegleMetierException("La référence de transaction bancaire est obligatoire");
        }

        this.statut = StatutCommande.PAYEE;
        this.referencePaiement = referenceTransaction;
        this.evenementsDomaine.add(new CommandePayeeEvent(
                this.id,
                this.emailClient,
                this.montantTotal,
                this.referencePaiement,
                Instant.now()
        ));
    }

    public void annuler() {
        if (this.statut == StatutCommande.PAYEE) {
            throw new RegleMetierException("Une commande déjà payée ne peut pas être annulée sans remboursement");
        }
        this.statut = StatutCommande.ANNULEE;
    }

    public List<CommandePayeeEvent> consommerEvenements() {
        List<CommandePayeeEvent> copie = List.copyOf(this.evenementsDomaine);
        this.evenementsDomaine.clear();
        return copie;
    }

    public CommandeId id() { return id; }
    public EmailClient emailClient() { return emailClient; }
    public List<LigneCommande> lignes() { return lignes; }
    public Montant montantTotal() { return montantTotal; }
    public StatutCommande statut() { return statut; }
    public String referencePaiement() { return referencePaiement; }
}
