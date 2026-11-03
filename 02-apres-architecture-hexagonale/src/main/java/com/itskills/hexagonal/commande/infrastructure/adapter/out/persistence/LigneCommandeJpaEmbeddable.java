package com.itskills.hexagonal.commande.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class LigneCommandeJpaEmbeddable {

    @Column(nullable = false)
    private String codeProduit;

    @Column(nullable = false)
    private int quantite;

    @Column(nullable = false)
    private BigDecimal prixUnitaire;

    protected LigneCommandeJpaEmbeddable() {
    }

    public LigneCommandeJpaEmbeddable(String codeProduit, int quantite, BigDecimal prixUnitaire) {
        this.codeProduit = codeProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public String getCodeProduit() { return codeProduit; }
    public int getQuantite() { return quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
}
