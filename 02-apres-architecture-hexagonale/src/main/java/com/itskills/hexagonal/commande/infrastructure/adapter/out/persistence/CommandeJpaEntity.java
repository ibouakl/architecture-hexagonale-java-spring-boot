package com.itskills.hexagonal.commande.infrastructure.adapter.out.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ENTITÉ TECHNIQUE JPA (@Entity) :
 * - Strictement confinée dans le package infrastructure.adapter.out.persistence.
 * - Peut utiliser toutes les contraintes d'Hibernate (constructeur vide, getters/setters)
 *   sans jamais polluer l'agrégat métier Commande !
 */
@Entity
@Table(name = "commandes_hexagonal")
public class CommandeJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String emailClient;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "commande_lignes_hexagonal", joinColumns = @JoinColumn(name = "commande_id"))
    private List<LigneCommandeJpaEmbeddable> lignes = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal montantTotal;

    @Column(nullable = false)
    private String statut;

    private String referencePaiement;

    protected CommandeJpaEntity() {
    }

    public CommandeJpaEntity(
            UUID id,
            String emailClient,
            List<LigneCommandeJpaEmbeddable> lignes,
            BigDecimal montantTotal,
            String statut,
            String referencePaiement) {
        this.id = id;
        this.emailClient = emailClient;
        this.lignes = lignes;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.referencePaiement = referencePaiement;
    }

    public UUID getId() { return id; }
    public String getEmailClient() { return emailClient; }
    public List<LigneCommandeJpaEmbeddable> getLignes() { return lignes; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public String getStatut() { return statut; }
    public String getReferencePaiement() { return referencePaiement; }
}
