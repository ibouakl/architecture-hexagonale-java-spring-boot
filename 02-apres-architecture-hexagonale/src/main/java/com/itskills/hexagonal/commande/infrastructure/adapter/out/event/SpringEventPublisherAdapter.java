package com.itskills.hexagonal.commande.infrastructure.adapter.out.event;

import com.itskills.hexagonal.commande.domain.event.CommandePayeeEvent;
import com.itskills.hexagonal.commande.domain.port.out.PublicationEvenementPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * ADAPTATEUR SECONDAIRE d'événements : publie les Domain Events sur le bus Spring (ou Kafka/RabbitMQ).
 */
@Component
public class SpringEventPublisherAdapter implements PublicationEvenementPort {

    private final ApplicationEventPublisher eventPublisher;

    public SpringEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publier(CommandePayeeEvent evenement) {
        eventPublisher.publishEvent(evenement);
    }
}
