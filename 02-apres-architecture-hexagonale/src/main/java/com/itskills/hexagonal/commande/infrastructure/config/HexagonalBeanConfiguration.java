package com.itskills.hexagonal.commande.infrastructure.config;

import com.itskills.hexagonal.commande.domain.port.out.CommandeRepositoryPort;
import com.itskills.hexagonal.commande.domain.port.out.PaiementGatewayPort;
import com.itskills.hexagonal.commande.domain.port.out.PublicationEvenementPort;
import com.itskills.hexagonal.commande.domain.service.GestionCommandeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CÂBLAGE D'INFRASTRUCTURE (@Configuration) :
 * Instancie le service du Domaine comme un bean Spring sans jamais mettre @Service ou @Autowired
 * dans le package domain !
 */
@Configuration
public class HexagonalBeanConfiguration {

    @Bean
    public GestionCommandeService gestionCommandeService(
            CommandeRepositoryPort commandeRepositoryPort,
            PaiementGatewayPort paiementGatewayPort,
            PublicationEvenementPort publicationEvenementPort) {
        return new GestionCommandeService(
                commandeRepositoryPort,
                paiementGatewayPort,
                publicationEvenementPort
        );
    }
}
