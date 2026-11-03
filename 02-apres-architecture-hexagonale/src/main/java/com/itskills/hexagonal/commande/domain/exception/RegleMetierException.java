package com.itskills.hexagonal.commande.domain.exception;

/**
 * Exception métier du domaine (100 % Java pur, aucune dépendance HTTP ou Spring).
 */
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}
