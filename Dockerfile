
FROM maven:3.8.5-openjdk-17 AS builder

# Copie du fichier pom.xml pour télécharger les dépendances
COPY pom.xml .

# Télécharger les dépendances de Maven
RUN mvn dependency:go-offline -B

# Copie des fichiers source
COPY src ./src

# Exécution de Maven pour construire le projet
RUN mvn -B clean package -DskipTests

# Utilisation d'une image de base pour exécuter l'application Java
FROM openjdk:17-jdk

# Définition d'un argument pour le nom du fichier JAR à copier
ARG JAR_FILE=target/*.jar

# Création d'un répertoire de travail dans le conteneur
WORKDIR /app

# Copie du fichier JAR généré par Maven dans le répertoire de travail du conteneur
COPY --from=builder ${JAR_FILE} application.jar

# Définition de la commande d'entrée pour exécuter l'application Java
ENTRYPOINT ["java", "-jar", "application.jar"]

