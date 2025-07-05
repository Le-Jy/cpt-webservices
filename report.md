# Rapport de (Mini-)Projet

## Justification de l’Architecture Applicative

Dans le cadre de ce projet de gestion automatique d’une maison connectée, nous avons conçu une architecture modulaire reposant sur plusieurs microservices, chacun responsable d’un capteur ou d’un actionneur (LED, capteur de luminosité, température, humidité, bouton, afficheur LCD). Ces services communiquent via des **webservices REST** et, dans certains cas, via **MQTT** pour répondre aux contraintes propres aux objets connectés.

---

## Choix de l’Architecture REST

Nous avons fait le choix de structurer notre application autour de **webservices REST**, pour plusieurs raisons :

###  Simplicité et compatibilité universelle

* REST repose sur le protocole HTTP, nativement supporté par le matériel utilisé (ESP8266 via `ESP8266HTTPClient`), les serveurs Spring Boot, les frontends web, et les outils de test comme Postman.
* Cela facilite le **développement, le test et l’intégration**.

###  Décomposition modulaire et claire

* Chaque service Spring Boot représente un composant de la maison : `Humidity Service`, `LED Service`, `Sunlight Service`, etc.
* Cela rend l’application **facile à maintenir** et **extensible**, en facilitant le **déploiement indépendant** de chaque service.

Nous avons réussi 

###  Standardisation

* REST suit des conventions standard (`GET`, `POST`, `PUT`, `DELETE`) qui facilitent la documentation, la compréhension de l’API, et son utilisation dans des scénarios tiers ou futurs projets.


## Ajout du Broker MQTT : justification technique

Nous avons également décidé d'intégrer un **broker MQTT (Mosquitto)** entre l'ESP et le backend. Bien que cela puisse introduire une **certaine complexité réseau**, ce choix est **parfaitement justifié dans un contexte IoT** :

###  MQTT est conçu pour les objets connectés

* L’ESP étant une carte à **faibles ressources**, la communication en HTTP pose rapidement des limites (taille des en-têtes, charge réseau, gestion des connexions TCP).
* MQTT est un **protocole léger et optimisé**, basé sur TCP/IP, et conçu pour fonctionner dans des environnements contraints.

###  Réduction de la charge réseau

* Au lieu d’envoyer des requêtes HTTP multiples et simultanées, les ESP peuvent simplement publier leurs données sur un **topic MQTT**, avec un minimum de charge réseau.
* Le backend n’a qu’à **s’abonner une seule fois** pour recevoir en temps réel les données de plusieurs capteurs.

###  Découplage producteur / consommateur

* Le modèle publish/subscribe permet un **découplage total entre les ESP et le backend**. Cela permet une plus grande flexibilité : les capteurs n’ont pas besoin de connaître les adresses des consommateurs de leurs données.
* Cette architecture **facilite l’ajout de nouveaux capteurs ou services** dans le futur, sans impacter le reste du système.

###  Adapté à l’IoT temps réel

* Pour des scénarios où les capteurs envoient des valeurs en continu, MQTT est **plus performant et réactif** que les requêtes REST répétées (polling).

