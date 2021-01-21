# Manuel utilisateur

## Login
Les utilisateurs et le mot de passe associé est défini dans le fichier `.env`, qui doit être placé
dans le même répertoire que l'exécutable.

![page de login](images/login.png)

## Gestion des droits
Une fois connecté, l'utilisateur arrive sur la liste des paiements. Il n'y a actuellement pas
de gestion de rôles dans l'application. Donc tous les utilisateurs peuvent effectuer les mêmes actions.

## Liste des paiements
Les paiements sont alimentés directement par Hello Asso (configuration à faire côté Hello Asso et coté Hellos).
Pour chaque paiement, deux actions sont disponibles : 
- supprimer le paiement dans Hellos (pas d'effet dans Hello Asso ni Cyclos) en cliquant sur l'icône "poubelle"
- effectuer le paiement dans Cyclos, en cliquant sur l'icône "main avec dollar"

Quand un paiement est effectué, le status est mis à jour en fonction de la réussite ou de l'échec du paiement dans Cyclos.
Si le paiement a été réalisé avec succès dans Cyclos, une coche verte remplace le bouton "main avec dollar", afin d'éviter les doubles paiements.

Deux autres boutons sont disponibles : un pour aller vers le panneau d'administration et un pour se déconnecter.

![liste paiements](images/liste-paiements.png)

## Panneau d'administration
Il permet principalement de mettre à jour différentes configurations :
- Activation des paiements de Cyclos
- Activation des paiements automatiques
- Destinataire(s) des emails d'Hellos

Un bouton permettant de récupérer manuellement les paiements dans Hello Asso est disponible. Il est utile en cas de défaillance côté Hello Asso (parfois les notifications n'arrivent pas)

![panneau admin](images/panneau-admin.png)

### Notification par emails
En cas d'erreur, un mail avec des informations techniques est envoyé.
En mode automatique, un mail est envoyé dans tous les cas, en cas d'échec comme en cas de succès.
