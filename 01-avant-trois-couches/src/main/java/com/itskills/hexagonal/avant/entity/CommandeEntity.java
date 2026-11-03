package com.itskills.hexagonal.avant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * ANTI-PATTERN : Modèle du domaine anémique couplé à JPA/Hibernate.
 * - Aucune règle métier dans la classe (simples getters/setters publics).
 * - N'importe quel service peut mettre un montant négatif ou un statut incohérent.
 */
@Entity
@Table(name = "commandes")
public class CommandeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String emailClient;

    @Column(nullable = false)
    private BigDecimal montantTotal;

    @Column(nullable = false)
    private String statut; // "CREEE", "PAYEE", "ANNULEE" en String libre !

    private String referencePaiement;

    // Constructeur vide obligatoire pour Hibernate
    public CommandeEntity() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmailClient() { return emailClient; }
    public void setEmailClient(String emailClient) { this.emailClient = emailClient; }

    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getReferencePaiement() { return referencePaiement; }
    public void setReferencePaiement(String referencePaiement) { this.referencePaiement = referencePaiement; }
}
