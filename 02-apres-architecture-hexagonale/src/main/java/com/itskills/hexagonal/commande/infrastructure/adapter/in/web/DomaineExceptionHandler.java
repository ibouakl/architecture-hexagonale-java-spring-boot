package com.itskills.hexagonal.commande.infrastructure.adapter.in.web;

import com.itskills.hexagonal.commande.domain.exception.CommandeIntrouvableException;
import com.itskills.hexagonal.commande.domain.exception.RegleMetierException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduit les exceptions métier pures du domaine en statuts HTTP normalisés (RFC 7807 ProblemDetail).
 */
@RestControllerAdvice
public class DomaineExceptionHandler {

    @ExceptionHandler(CommandeIntrouvableException.class)
    public ProblemDetail gererCommandeIntrouvable(CommandeIntrouvableException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RegleMetierException.class)
    public ProblemDetail gererViolationRegleMetier(RegleMetierException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }
}
