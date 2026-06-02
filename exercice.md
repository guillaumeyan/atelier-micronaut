# Atelier Micronaut - The Matrix

## Introduction

L'objectif de cet atelier est de construire une API REST permettant d'organiser des combats entre équipes de personnages de l'univers Matrix.

Vous allez créer le projet depuis zéro, puis implémenter progressivement la couche infrastructure : contrôleurs, persistance, client HTTP et messaging
Kafka.

### Prérequis

- JDK 25
- Docker et Docker Compose
- Un IDE (IntelliJ IDEA recommandé)
- Un client HTTP pour tester (Bruno, collection fournie dans le dossier `Bruno/`)

---

## 1. Mise en place de l'environnement

### Étape 1 : Générer le projet

Rendez-vous sur [Micronaut Launch](https://micronaut.io/launch/) et créez un projet avec les paramètres suivants :

- **Application Type** : Micronaut Application
- **Java Version** : 25
- **Micronaut Version** : 4.x
- **Language** : Java
- **Build Tool** : Maven
- **Test Framework** : JUnit
- **Base Package** : io.shodo.matrix

Ajoutez les features suivantes :

- `postgres`
- `data-jdbc`
- `flyway`
- `testcontainers`
- `assertj`
- `validation`
- `yaml`
- `kafka`
- `management`
- `lombok`
- `httpclient`

### Étape 2 : Intégrer le domaine

Copiez le contenu du package `domain/` fourni dans votre projet, sous `src/main/java/io/shodo/matrix/domain/`.

Implémenter [PersonnageRepository.java](src/main/java/io/shodo/matrix/domain/spi/PersonnageRepository.java) avec une valeur par défaut.

### Étape 3 : Ajouter la migration de données

Placez le fichier `V1__insert_matrix_data.sql` fourni dans `src/main/resources/db/migration/`.

### Étape 4 : Démarrer l'environnement

Lancez les dépendances :

```bash
docker compose up -d
```

Cela démarre :

- **PostgreSQL** sur le port `5432`
- **WireMock** (mock de l'API environnement) sur le port `8081`
- **Kafka** sur le port `9092`
- **Kafka UI** sur http://localhost:8090 (interface pour visualiser les topics et messages)

Démarrer l'application

---

## 2. Création d'une API REST

L'objectif est de créer un contrôleur permettant d'affronter 2 équipes de personnages.

> Documentation : [Micronaut HTTP Server - Controllers](https://docs.micronaut.io/latest/guide/#httpServer)

### Étape 1 : Créer le contrôleur

Créez un contrôleur avec le endpoint racine `/matrix` :

1. **GET /matrix/personnages** : récupère tous les personnages sans leurs relations en utilisant la pagination.
2. **POST /matrix/combat** : accepte 2 listes de noms de personnages et appelle `ZoneCombatApi.combattre`

> Documentation : [Pagination dans Micronaut](https://docs.micronaut.io/latest/guide/#pagination)

> Consultez la collection Bruno pour voir la structure des requêtes attendue.

### Étape 2 : Ajouter les tests

Écrivez des tests pour vos endpoints en utilisant `@MicronautTest` :

- Mockez `ZoneCombatApi` et `JdbcPersonnageRepository` avec `@MockBean` (Mockito)
- Injectez le `HttpClient` pour appeler vos endpoints
- Vérifiez les réponses HTTP (status, body)

>
Documentation : [Écrire un test Micronaut avec JUnit 5](https://micronaut-projects.github.io/micronaut-test/latest/guide/#writingAMicronautTestWithJUnit5)
> Documentation MockBean : [Micronaut MockBean](https://docs.micronaut.io/latest/guide/#mockingBeans)

### Étape 3 : Ajouter la validation

Utilisez la validation Micronaut pour vérifier que les listes de personnages ne sont pas vides. Ajoutez les tests correspondants.

> Documentation : [Micronaut Validation](https://micronaut-projects.github.io/micronaut-validation/latest/guide/)

### Étape 4 : Gestion des erreurs locale

Ajoutez un handler d'erreur pour `PersonnageNotFoundException` qui retourne une réponse **404**.

> Documentation : [Gestion d'erreur locale](https://docs.micronaut.io/latest/guide/#localErrorHandling)

### Étape 5 : Gestion des erreurs globale

Ajoutez un handler d'erreur global qui retourne le message : *"Erreur détectée. Cette version de la réalité n'est pas supportée."*

> Documentation : [Gestion d'erreur globale](https://docs.micronaut.io/latest/guide/#globalErrorHandling)

Ajoutez les tests pour les deux handlers.

---

## 3. Accès aux données et persistance

> Documentation : [Micronaut Data](https://micronaut-projects.github.io/micronaut-data/latest/guide/)

### Étape 1 : Créer les entités

Créez les entités correspondant aux personnages, vaisseaux et compétences. Annotations utiles :

- `@MappedEntity`
- `@Id`
- `@GeneratedValue`
- `@Version`
- `@DateCreated` / `@DateUpdated`

**Ne pas ajouter les relations entre entités pour l'instant.**

### Étape 2 : Créer le repository

Créez `JdbcPersonnageRepository` avec les méthodes :

- Récupérer les personnages à partir d'une liste de noms (sans relations)
- Récupérer tous les personnages

### Étape 3 : Tester le repository

Écrivez un test avec **Micronaut Test Resources** pour valider le fonctionnement du repository. Le provisioning de la base PostgreSQL se fait
automatiquement — pas besoin de configurer Testcontainers manuellement.

> Documentation : [Micronaut Test Resources](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/)

### Étape 4 : Implémenter l'adaptateur PersonnageRepository

Implémentez les méthodes de `PersonnageRepository` (l'interface SPI du domaine). Utilisez `@Mapper` de Micronaut pour le mapping entre entités et
objets du domaine.

Ajoutez les tests nécessaires.

### Étape 5 : Ajouter les relations

Ajoutez les relations avec `@Relation`. Créez une requête pour récupérer les personnages avec leurs compétences et vaisseaux.

Ajouter les tests pour `JdbcPersonnageRepository`.

> Documentation : [Micronaut Data - Relations](https://micronaut-projects.github.io/micronaut-data/latest/guide/#hibernateJoinQueries)

### Étape 6 : Test end-to-end

Branchez tout ensemble et testez depuis `/matrix/combat`. Vous pouvez utiliser la collection Bruno fournie dans le dossier `Bruno/`.

---

## 4. HTTP Client

> Documentation : [Micronaut HTTP Client](https://docs.micronaut.io/latest/guide/#clientBasics)

Implémentez `EnvironnementCombatProvider` en utilisant le client HTTP déclaratif de Micronaut pour appeler l'API d'environnement (servie par WireMock
sur le port 8081).

Consultez la collection Bruno pour voir la structure de la réponse attendue.

---

## 5. Intégration Kafka

> Documentation : [Micronaut Kafka](https://micronaut-projects.github.io/micronaut-kafka/latest/guide/#kafkaClient)

### Étape 1 : Configuration

Ajoutez dans `application.yml` la configuration pour se connecter au broker Kafka :

```yaml
kafka:
  bootstrap:
    servers: localhost:9092
```

### Étape 2 : Créer le producer

Créez le producer avec une méthode qui envoie un message sur le topic `reporting-combat`.

### Étape 3 : Implémenter ReportingProviderAdapter

Créez l'adaptateur qui implémente `ReportingProvider` (interface SPI du domaine). Cet adaptateur :

- Reçoit la liste des personnages gagnants et perdants
- Transforme ces données en un objet de reporting (noms, vaisseaux, compétences de chaque équipe)
- Envoie le message via le producer Kafka

### Étape 4 : Vérification manuelle

1. Lancez l'application
2. Exécutez un combat via `/matrix/combat`
3. Ouvrez **Kafka UI** sur http://localhost:8090
4. Naviguez vers le topic `reporting-combat` et vérifiez que le message a bien été produit

### Étape 5 : Tester le producer

Écrivez un test d'intégration avec **Testcontainers** et un broker Kafka embarqué :

- Démarrez un conteneur Kafka via Testcontainers
- Exécutez un combat
- Consommez le message sur le topic `reporting-combat` (via un `@KafkaListener` de test)
- Vérifiez que le message contient les bonnes données : noms des gagnants/perdants, vaisseaux, compétences

> Documentation : [Micronaut Kafka Testing](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/#modules-kafka)
