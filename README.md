
# Projet Etudiant Openclassrooms n°2 – Testez une application Java

<img src="/preview.jpg" alt="Logo de l'application">

<h1 align="center">Park'It - Parking System</h1>

<br>
Park'It est une application de gestion de parking qui permet de gérer les entrées et sorties des véhicules dans un parking.

## Fonctionnalités

- Gestion des entrées et sorties des véhicules (voitures, motos)
- Calcul automatique des frais de stationnement en fonction de la durée du stationnement
- Gestion des places de parking disponibles
- Prise en charge des utilisateurs récurrents avec des tarifs réduits

## Prérequis

- Java 8 ou supérieur
- Maven
- Un système de gestion de base de données MySQL

## Installation

1. Clonez le dépôt GitHub sur votre machine locale en utilisant la commande suivante :

```bash
git clone https://github.com/Xenophee/dassonville-perrine-tester-java.git
```

2. Importer le projet dans votre IDE.

3. Configurez votre base de données SQL. Créez une nouvelle base de données et exécutez le script SQL fourni dans le répertoire `resources` pour créer les tables nécessaires.

4. Mettez à jour le fichier `src/main/java/com.parkit.parkingsystem/config/DataBaseConfig` avec les informations de connexion à votre base de données.

5. Faites de même pour le fichier `src/test/java/com.parkit.parkingsystem/config/DataBaseConfigTest` pour les tests.

6. Exécutez le fichier `App.java` pour lancer l'application.



## Tests

Les tests peuvent être exécutés en utilisant la commande suivante :

```bash
mvn test
```
Les rapports de test seront générés dans le répertoire `target/surefire-reports`.

<br>

Pour obtenir un rapport de test en html, exécutez la commande suivante :
```bash
mvn surefire-report:report
```
Le résultat sera dans le répertoire `target/site/surefire-report.html`.

<br>

Pour vérifier la couverture des tests, exécutez la commande suivante :
```bash
mvn verify
```

Le résultat de la couverture des tests sera généré dans le répertoire `target/site/jacoco/index.html`.