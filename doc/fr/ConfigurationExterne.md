# Configuration externe

Afin de pouvoir utiliser cette application en production, il est nécessaire d'avoir des accès Hello Asso et Cyclos.

La gestion des secrets se fait via le fichier `.env`. Un template nommé `.env.dist` existe à la racine du projet. Il permet de connaitre les propriétés à définir.
Vous devez créer un fichier nommé `.env` à la racine du projet et le peupler avec les propriétés adéquates.

## Hello Asso
Pour Hello Asso, il vous faut générer des identifiants pour l'API et définir l'URL de callback des notifications.
Pour cela, il faut aller dans "Mon Compte"/"Intégrations et API". Là vous pouvez générer un clientId et un clientSecret.
L'URL de callback est également paramétrable dans cette page. Il s'agit de l'URL de votre serveur, plus `helloasso/payment` (défini dans la classe `PaymentInputController`).

Une fois ces informations récupérées, vous devez les rajouter dans votre fichier `.env`.
Les propriétés  associées sont `HELLO_ASSO_CLIENT_ID` et `HELLO_ASSO_CLIENT_SECRET`.

Il faut également définir les autres propriétés préfixées par `HELLO_ASSO` présentes dans le fichier `.env.dist`.

## Cyclos
Pour Cyclos, vous allez devoir créer un utilisateur technique (meilleurs pratique que d'utiliser un compte administrateur déjà existant et associé à une personne physique).
Le plus simple est de créer un nouveau groupe (nommé "Utilisateur technique" par exemple) et d'y associer les bons droits.
À rajouter :
* l'autorisation au canal 'Web services'
* l'autorisation de paiement "système vers utilisateurs"
* l'accès aux différents groupes des utilisateurs pouvant recevoir des paiements

Si vous voyez un paramétrage manquant, n'hésitez pas à le signaler en créant une issue.

Il vous faut également ajouter dans le `.env` toutes les propriétés présentes dans le `.env.dist` et préfixées par `CYCLOS`.
`CYCLOS_USER` correspond à l'identifiant interne de l'utilisateur technique (suite de chiffres générée en interne par Cyclos).
`CYCLOS_GROUP_PRO_INTERNAL` correspond au nom interne du groupe des professionnels.
`CYCLOS_EMISSION_PRO_INTERNAL` correspond au nom interne de l'émission pour les professionnels.
(idem pour les particuliers)