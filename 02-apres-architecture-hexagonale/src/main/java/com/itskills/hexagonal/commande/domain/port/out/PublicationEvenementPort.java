package com.itskills.hexagonal.commande.domain.port.out;

import com.itskills.hexagonal.commande.domain.event.CommandePayeeEvent;

/**
 * PORT SECONDAIRE (Driven / SPI) : Publication des événements du domaine vers d'autres Bounded Contexts.
 */
public interface PublicationEvenementPort {

    void publier(CommandePayeeEvent evenement);
}
