# Architecture Hexagonale, Clean Architecture & DDD en Java — Projet Fil Rouge

[![Java](https://img.shields.io/badge/Java-21%2B%20%2F%2025-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![ArchUnit](https://img.shields.io/badge/ArchUnit-1.4.1-blue.svg)](https://www.archunit.org/)
[![Udemy](https://img.shields.io/badge/Udemy-Formation%20en%20ligne-red.svg?logo=udemy)](https://www.udemy.com/course/architecture-hexagonale-clean-architecture-ddd-en-java/?referralCode=0CB398B9A8EC01A64004)
[![Build Status](https://img.shields.io/badge/Tests-9%20passed%20%2F%200%20failed-success.svg)]()

Ce dépôt contient le code source complet, compilé et testé qui accompagne la formation vidéo Udemy :  
👉 [**« Architecture Hexagonale, Clean Architecture & DDD en Java (Spring Boot Avant / Après) »**](https://www.udemy.com/course/architecture-hexagonale-clean-architecture-ddd-en-java/?referralCode=0CB398B9A8EC01A64004).

Il illustre pas à pas le refactoring d'une application de gestion de commandes et paiements, depuis un monolithe 3-couches couplé jusqu'à une architecture hexagonale pure et hautement testable.

---

## 🏗️ Structure du projet multi-modules

```text
code-exemple/
├── pom.xml                                   # POM parent orchestrant les deux modules
├── 01-avant-trois-couches/                   # ⚠️ Le modèle couplé traditionnel
└── 02-apres-architecture-hexagonale/         # ✨ L'architecture hexagonale refactorée
```

### Comparatif Avant / Après

| Critère | Module `01-avant-trois-couches` | Module `02-apres-architecture-hexagonale` |
|---|---|---|
| **Structure** | `Controller -> Service -> Repository` | `Domain (pur)` $\leftarrow$ `Ports` $\leftarrow$ `Adapters (Spring/JPA/REST)` |
| **Modèle métier** | **Anémique** (`CommandeEntity` avec getters/setters) | **Riche (DDD)** (`Commande` Aggregate Root, invariants protégés) |
| **Types primitifs** | Primitive Obsession (`String email`, `BigDecimal montant`) | **Value Objects** (`EmailClient`, `Montant`, `CommandeId`) |
| **Couplage technique**| `@Entity`, `@Transactional`, Hibernate partout | **0 dépendance framework** dans le package `domain` |
| **Vitesse des tests** | Tests lents `@SpringBootTest` (~15 s de boot) | **Tests unitaires purs en 5 ms** + **Fakes en mémoire** |
| **Garde-fous** | Aucun (dérive d'architecture fréquente) | **ArchUnit** automatisé à chaque build |

---

## 🏛️ Arborescence détaillée du Cœur Hexagonal (`02-apres-architecture-hexagonale`)

```text
com.itskills.hexagonal.commande
│
├── domain                                   # 🟢 CŒUR MÉTIER (100 % Java pur — 0 dépendance Spring / JPA)
│   ├── model
│   │   ├── Commande.java                    # Aggregate Root (protège les règles métier et transitions d'état)
│   │   ├── CommandeId.java                  # Value Object (UUID typé non nul)
│   │   ├── EmailClient.java                 # Value Object auto-validant (record)
│   │   ├── Montant.java                     # Value Object monétaire immuable (record + calculs BigDecimal sûrs)
│   │   ├── LigneCommande.java               # Value Object représentant une ligne d'article
│   │   └── StatutCommande.java              # Enum métier (CREEE, PAYEE, ANNULEE)
│   ├── event
│   │   └── CommandePayeeEvent.java          # Domain Event immuable émis lors du règlement
│   ├── exception
│   │   ├── RegleMetierException.java        # Exception métier pure
│   │   └── CommandeIntrouvableException.java
│   ├── port
│   │   ├── in                               # 🔵 PORTS PRIMAIRES (Driving / Inbound / API)
│   │   │   ├── PasserCommandeUseCase.java   # Cas d'usage : création de commande
│   │   │   └── PayerCommandeUseCase.java    # Cas d'usage : règlement sécurisé
│   │   └── out                              # 🟠 PORTS SECONDAIRES (Driven / Outbound / SPI)
│   │       ├── CommandeRepositoryPort.java  # Contrat de persistance
│   │       ├── PaiementGatewayPort.java     # Contrat de passerelle de paiement (ResultatPaiement scellé)
│   │       └── PublicationEvenementPort.java # Contrat de publication d'événements
│   └── service
│       └── GestionCommandeService.java      # Orchestrateur des cas d'usage (zéro annotation Spring)
│
└── infrastructure                           # 🔴 DÉTAILS TECHNIQUES & FRAMEWORKS
    ├── adapter
    │   ├── in.web                           # Adaptateur Primaire REST (@RestController, RFC-7807 ProblemDetail)
    │   │   ├── CommandeRestController.java
    │   │   ├── PasserCommandeRequest.java
    │   │   ├── CommandeResponse.java
    │   │   └── DomaineExceptionHandler.java # Traduction des exceptions métier en HTTP 400 / 404
    │   ├── out.persistence                  # Adaptateur Secondaire JPA
    │   │   ├── CommandeJpaEntity.java       # Entité JPA pure avec tables et clés étrangères
    │   │   ├── CommandeJpaMapper.java       # Mapper bidirectionnel Domaine <-> Entité JPA
    │   │   ├── SpringDataCommandeRepository.java
    │   │   └── CommandeJpaAdapter.java      # Implémentation du Port CommandeRepositoryPort
    │   ├── out.payment                      # Adaptateur Secondaire + Anti-Corruption Layer (ACL Stripe)
    │   │   ├── StripeChargePayload.java     # Modèle technique externe Stripe (centimes USD/EUR)
    │   │   └── StripePaiementAclAdapter.java # Traducteur / Barrière anti-corruption
    │   └── out.event                        # Adaptateur Secondaire Événements
    │       └── SpringEventPublisherAdapter.java # Pont vers ApplicationEventPublisher Spring
    └── config
        └── HexagonalBeanConfiguration.java  # Câblage Spring explicite (@Configuration / @Bean)
```

---

## 🧪 Stratégie de Test (Pyramide Inversée & BDD Given-When-Then)

Tous les tests du projet sont écrits selon le pattern BDD **Given-When-Then** avec des `@DisplayName` explicites en français :

1. **Tests unitaires du Domaine (`CommandeDomainTest`)** :
   - Testent les règles de remise VIP, la validation des invariants et les transitions d'état.
   - **Vitesse : < 10 millisecondes**, sans Spring ni Mockito.
2. **Tests de Cas d'Usage avec Fakes (`GestionCommandeUseCaseTest`)** :
   - Valident l'orchestration complète en utilisant un `InMemoryCommandeRepositoryFake` (basé sur une simple `HashMap`).
   - Évitent les mocks fragiles (`verify`, `when`).
3. **Tests d'Adaptateur Tranche JPA (`CommandeJpaAdapterIntegrationTest`)** :
   - Testent fidèlement le mapping SQL et la persistance avec `@DataJpaTest` et H2, sans charger toute l'application.
4. **Vérification d'Architecture (`HexagonalArchitectureArchUnitTest`)** :
   - Règle 1 : *Le Domaine ne doit jamais dépendre de l'Infrastructure*.
   - Règle 2 : *Le Domaine ne doit importer aucun package Spring, Hibernate ou Jakarta Persistence*.

---

## 🚀 Prérequis & Lancement

### Prérequis
- **Java 21** ou supérieur (testé et compatible **Java 25**).
- **Maven 3.9+**.

### Cloner et exécuter les tests
```bash
git clone https://github.com/ibouakl/architecture-hexagonale-java-spring-boot.git
cd architecture-hexagonale-java-spring-boot

# Compiler et lancer la suite complète de tests (Avant + Après + ArchUnit)
mvn clean test
```

---

## 👨‍🏫 Auteur & Formation

Formation conçue et produite par **Imad BOU AKL** pour **ItSkillsMaker**.  
🎓 Retrouvez le cours complet sur [Udemy : Architecture Hexagonale, Clean Architecture & DDD en Java](https://www.udemy.com/course/architecture-hexagonale-clean-architecture-ddd-en-java/?referralCode=0CB398B9A8EC01A64004).
