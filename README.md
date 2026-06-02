# Atelier Micronaut - The Matrix

## Introduction

L'objectif de cet atelier est de construire une API REST permettant d'organiser des combats entre équipes de personnages de l'univers Matrix.

Vous allez créer le projet depuis zéro, puis implémenter progressivement la couche infrastructure : contrôleurs, persistance, client HTTP et messaging
Kafka.

### Prérequis

- JDK 25
- Docker et Docker Compose
- Un IDE
- Un client HTTP pour tester (Bruno, collection fournie dans le dossier `Bruno/`)

---

## 1. Mise en place de l'environnement


### Étape 1 : Démarrer l'environnement

> **Ressources fournies (clé USB)** : une clé USB circule avec le repository, les
> dépendances Maven (à copier dans votre `~/.m2`) et les images Docker
> (à charger via `docker load -i matrix-images.tar`). Cela évite de tout
> télécharger sur place. Vous pouvez aussi tout récupérer vous-même via internet
> (clone du repo, `./mvnw` télécharge les dépendances, `docker compose` tire les images).

Démarrer docker

Lancez les dépendances :

```bash
docker compose up -d
```

Cela démarre :

- **PostgreSQL** sur le port `5432`
- **WireMock** (mock de l'API environnement de combat) sur le port `8081`. Les stubs sont dans le dossier `wiremock/mappings`
- **Kafka** sur le port `9092`
- **Kafka UI** sur http://localhost:8090 (interface pour visualiser les topics et messages)

Démarrer l'application :

`./mvnw mn:run`

ou après packaging :

`java -jar target/atelier-micronaut-1.0-SNAPSHOT.jar`

### Étape 2 : Prendre connaissance de l'architecture

Prendre connaissance de la structure du projet et des différentes classes mises à disposition.

Analyser les dépendances Micronaut, avec une attention particulière portée au plugin maven-compiler-plugin.

Étudier le fichier de configuration application.yml afin de comprendre les paramètres de l'application.

Examiner le script `V1__insert_matrix_data.sql` situé dans `src/main/resources/db/migration/` pour appréhender le modèle et la structure de la base de données.

Tester l'application en explorant les endpoints d'administration exposés par Micronaut, tels que :
* http://localhost:8080/health
* http://localhost:8080/info
* http://localhost:8080/beans

> [endpoints management](https://docs.micronaut.io/latest/guide/#providedEndpoints)

### Étape 3 (optionnelle) : Bruno

Ajouter les requêtes dans Bruno, elles sont disponibles dans le dossier `bruno`

---

## 2. Création d'une API REST

L'objectif est de créer un contrôleur permettant d'affronter 2 équipes de personnages.

### Étape 1 : Créer le contrôleur

Créez un contrôleur avec le endpoint racine `/matrix` :

1. **GET /matrix/personnages** : récupère tous les personnages sans leurs relations en utilisant la pagination.
2. **POST /matrix/combat** : accepte 2 listes de noms de personnages et appelle `ZoneCombatApi.combattre`

> [Micronaut HTTP Server - Controllers](https://docs.micronaut.io/latest/guide/#httpServer)

> [Pagination dans Micronaut](https://docs.micronaut.io/latest/guide/#pagination)

Consultez la collection Bruno pour voir la structure des requêtes attendue.

### Étape 2 : Ajouter les tests

Écrivez des tests pour vos endpoints en utilisant `@MicronautTest` :

- Mockez `ZoneCombatApi` avec `@MockBean` (Mockito)
- Injectez le `HttpClient` pour appeler vos endpoints
- Vérifiez les réponses HTTP (status, body)

Pensez à désactiver la datasource et flyway dans l'`application-test.yml` :
```yaml
datasources.default.enabled: false
flyway.datasources.default.enabled: false
```

> [Écrire un test Micronaut avec JUnit 5](https://micronaut-projects.github.io/micronaut-test/latest/guide/#writingAMicronautTestWithJUnit5)

> [Micronaut MockBean](https://docs.micronaut.io/latest/guide/#mockingBeans)

### Étape 3 : Ajouter la validation

Utilisez la validation Micronaut pour vérifier que les listes de personnages ne sont pas vides. Ajoutez les tests correspondants.

> [Micronaut Validation](https://micronaut-projects.github.io/micronaut-validation/latest/guide/)

### Étape 4 : Gestion des erreurs globale

Créer un contrôleur global pour gérer toutes les erreurs et retourner le message : *"Erreur détectée. Cette version de la réalité n'est pas supportée."*

Optionnelle :

Ajoutez un handler d'erreur global quand une exception ConstraintViolationException est levée (validation) qui retourne le message : *"Ces paramètres n’existent pas dans cette réalité."* avec un code 400.

> [Gestion d'erreur globale](https://docs.micronaut.io/latest/guide/#globalErrorHandling)

Ajoutez les tests.

### Étape 5 : Gestion des erreurs locale

Ajoutez un handler d'erreur local pour `PersonnageNotFoundException` qui retourne une réponse **404**.

> [Gestion d'erreur locale](https://docs.micronaut.io/latest/guide/#localErrorHandling)

Ajouter le test correspondant.

---

## 3. Accès aux données et persistance

> [Micronaut Data](https://micronaut-projects.github.io/micronaut-data/latest/guide/)

> [Micronaut Data JDBC](https://micronaut-projects.github.io/micronaut-data/latest/guide/#dbc)

### Étape 1 : Créer les entités

Créez les entités correspondant aux personnages, vaisseaux et compétences. Annotations utiles :

- `@MappedEntity`
- `@Id`
- `@AutoPopulated` => Permet d'auto généré les UUID
- `@Relation`
- `@DateCreated`
- `@DateUpdated`

> [Micronaut Data - Annotations](https://micronaut-projects.github.io/micronaut-data/latest/guide/#sqlAnnotations)

> [Micronaut Data - Relations](https://micronaut-projects.github.io/micronaut-data/latest/guide/#sqlAssociationMapping)

### Étape 2 : Créer le repository

Créez `JdbcPersonnageRepository` avec les méthodes :

- Récupérer les personnages à partir d'une liste de noms
- Récupérer tous les personnages sous forme de pagination

ℹ️ Vous pouvez aller voir les requêtes générées par Micronaut Data après la compilation dans le dossier target, rechercher : $JdbcPersonnageRepository$Intercepted$Definition$Exec

ℹ️ Les logs SQL sont activés dans le fichier `application.yml` avec la propriété `logger.levels.io.micronaut.data: TRACE`

> [Pagination dans Micronaut](https://docs.micronaut.io/latest/guide/#pagination)

> [Jointure](https://micronaut-projects.github.io/micronaut-data/latest/guide/#dbcJoinQueries)

### Étape 3 : Tester le repository

Écrivez des tests pour valider le fonctionnement du repository. Pour ce test, on va utiliser test container. Le provisioning de la base PostgreSQL se fait
automatiquement en ajoutant des properties dans l'`application-test.yml`. Le démarrage du conteneur se fait automatiquement grâce `micronaut-test-resources`

Remplacer les properties dans `application-test.yml` :

```yaml
datasources:
  default:
    url: jdbc:tc:postgresql:16:///matrix_db
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
```

> Documentation : [Micronaut Test Resources](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/)

> Documentation jdbc : [Micronaut Test Resources - JDBC](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/#modules-databases-jdbc)

### Étape 4 : Implémenter l'adaptateur de PersonnageRepository

Compléter les méthodes de `PersonnageRepositoryAdapter`. Utilisez `@Mapper` de Micronaut pour le mapping entre entités et objets du domaine.

Ajoutez les tests pour le mapper.

### Étape 5 : Test end-to-end

Branchez tout ensemble et testez depuis `/matrix/combat`. Vous pouvez utiliser la collection Bruno fournie dans le dossier `bruno/`.

---

## 4. HTTP Client

> Documentation : [Micronaut HTTP Client](https://docs.micronaut.io/latest/guide/#clientBasics)

Implémentez `EnvironnementCombatProvider` en utilisant le client HTTP déclaratif de Micronaut pour appeler l'API d'environnement (servie par WireMock
sur le port 8081).

Consultez la collection Bruno pour voir la structure de la réponse attendue.

Vous pouvez tester depuis `/matrix/combat` pour vérifier que l'environnement est bien récupéré depuis l'API d'environnement.

---

## 5. Intégration Kafka

> Documentation : [Micronaut Kafka](https://micronaut-projects.github.io/micronaut-kafka/latest/guide/#kafkaClient)

### Étape 1 : Créer le producer

Créez le producer avec une méthode qui envoie un message sur le topic `reporting-combat`.

### Étape 2 : Implémenter ReportingProviderAdapter

Créez l'adaptateur qui implémente `ReportingProvider` (interface SPI du domaine). Cet adaptateur :

- Reçoit la liste des personnages gagnants et perdants
- Transforme ces données en un objet de reporting (noms, vaisseaux, compétences de chaque équipe)
- Envoie le message via le producer Kafka

### Étape 3 : Vérification manuelle

1. Lancez l'application
2. Exécutez un combat via `/matrix/combat`
3. Ouvrez **Kafka UI** sur http://localhost:8090
4. Naviguez vers le topic `reporting-combat` et vérifiez que le message a bien été produit

### Étape 4 : Tester le producer

Écrivez un test d'intégration avec Testcontainers et un broker Kafka embarqué :

- Démarrez un conteneur Kafka via Testcontainers
- Exécutez un combat
- Consommez le message sur le topic `reporting-combat` (via un `@KafkaListener` de test)
- Vérifiez que le message contient les bonnes données : noms des gagnants/perdants, vaisseaux, compétences

> Documentation : [Micronaut Kafka Testing](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/#modules-kafka)
