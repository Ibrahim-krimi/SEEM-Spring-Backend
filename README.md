# Documentation API 

## Authentification via Google
URL : /api/auth/google

Méthode : POST

Description : Authentifie un utilisateur en utilisant un token Google et renvoie un JWT si l’authentification réussit.

Corps de la requête :

```json
{
  "access_token": "<Token d'accès Google>",
  "email": "<Email de l'utilisateur>"
}
```

Réponse :

```json
{
  "token": "<JWT>",
  "user": {
    "id": "<ID utilisateur>",
    "name": "<Nom>",
    "email": "<Email>",
    "age": "<Âge>",
    "phoneNumber": "<Numéro de téléphone>",
    "country": "<Pays>",
    "city": "<Ville>",
    "bio": "<Bio>",
    "interests": ["<Intérêt 1>", "<Intérêt 2>"],
    "emailValid": "<Validation email>",
    "phoneNumberValid": "<Validation numéro>",
    "images": ["<URL image 1>", "<URL image 2>"]
  }
}
```


400 Bad Request :

```json
{
  "error": "Message d'erreur spécifique"
}
```
## Endpoints User Information 

## Récupérer les Informations d’un Utilisateur

URL : /user/information/{id}

Méthode : GET

Description : Récupère les informations d'un utilisateur spécifique.

Paramètres :

id : ID de l'utilisateur (dans l’URL)

Réponse :

200 OK : Renvoie les informations utilisateur.

404 Not Found : L’utilisateur n’existe pas.

400 Bad Request : Erreur dans les paramètres de la requête.

## Mettre à Jour Partiellement les Informations d’un Utilisateur

URL : /user/updateUserInformation

Méthode : PATCH

Description : Met à jour partiellement les informations d’un utilisateur.

Corps de la requête :

```json
{
  "id": "<ID utilisateur>",
  "name": "<Nom>",
  "email": "<Email>",
  "age": "<Âge>",
  "phoneNumber": "<Numéro de téléphone>",
  "country": "<Pays>",
  "city": "<Ville>",
  "bio": "<Bio>",
  "interests": ["<Intérêt 1>", "<Intérêt 2>"],
  "emailValid": "<Validation email>",
  "phoneNumberValid": "<Validation numéro>",
  "images": ["<URL image 1>", "<URL image 2>"],
  "viewedProfiles": ["<ID profil vu 1>", "<ID profil vu 2>"],
  "gender": "<Genre>",
  "lookingForGenders": "<Genre recherché>",
  "likes": ["<ID utilisateur liké 1>", "<ID utilisateur liké 2>"],
  "matches": ["<ID match 1>", "<ID match 2>"]
}

```


Réponse :

200 OK : Retourne les informations mises à jour de l'utilisateur.

400 Bad Request : ID utilisateur manquant ou autres erreurs.



## Première Connexion Utilisateur (Ajouter Photos et Mettre à Jour Infos)

URL : /user/{id}/FirstConnection

Méthode : POST

Description : Met à jour les informations utilisateur et ajoute des photos lors de la première connexion.

Paramètres :

id : ID de l'utilisateur (dans l’URL)

Corps de la requête :

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 30,
  "phoneNumber": "+123456789",
  "country": "France",
  "city": "Paris",
  "bio": "Développeur passionné par les nouvelles technologies.",
  "interests": ["cinéma", "football", "technologie"],
  "emailValid": true,
  "phoneNumberValid": true
}
```

photos : Liste des fichiers photos (multipart/form-data)

updates : Autres informations utilisateur à mettre à jour

Réponse :

200 OK : Retourne les informations mises à jour de l'utilisateur avec un message de confirmation.

400 Bad Request : Paramètres incorrects ou manquants.

500 Internal Server Error : Erreur lors du traitement des photos.

## Supprimer des Photos d’un Utilisateur Mettre à Jour les Photos de Profil


URL : /user/{id}/updatePhoto

Méthode : PATCH

Description : Met à jour les photos d’un utilisateur en ajoutant de nouvelles photos et/ou en supprimant certaines.

Paramètres :

id : ID de l'utilisateur (dans l’URL)

Corps de la requête :

photos : Liste des nouveaux fichiers photos (multipart/form-data)

PathphotoDelete : Liste des chemins des photos à supprimer

Réponse :

200 OK : Photos mises à jour avec succès.

400 Bad Request : Paramètres incorrects ou manquants.

500 Internal Server Error : Erreur lors de la mise à jour des photos


## Supprimer des Photos d’un Utilisateur



URL : /user/{id}/deletePhoto

Méthode : DELETE

Description : Supprime les photos spécifiques d’un utilisateur.

Paramètres :

id : ID de l'utilisateur (dans l’URL)

Corps de la requête :

```json
{
  "photoUrls": [
    "https://cdn.example.com/photos/user1/photo1.jpg",
    "https://cdn.example.com/photos/user1/photo2.jpg"
  ]
}
```

photoUrls : Liste des URLs des photos à supprimer

Réponse :

200 OK : Photos supprimées avec succès.

500 Internal Server Error : Erreur lors de la suppression de certaines photos.



## Documentation API - Gestion des Profils, Likes et Matchs

# Introduction

Ce contrôleur gère la recherche de profils en fonction des préférences de genre, l’ajout de likes, la récupération des matchs et la mise à jour du statut de match entre utilisateurs.

## Endpoints

## Récupérer des Profils Aléatoires

URL : /service/getPorfile

Méthode : GET

Description : Récupère une liste de profils aléatoires correspondant aux préférences de genre d'un utilisateur.

Paramètres de requête :

id : ID de l'utilisateur effectuant la recherche

genre : Genre recherché (ex. "femme" ou "homme")

nombre : Nombre de profils à retourner

## Liker un Profil


URL : /service/like

Méthode : GET

Description : Permet à un utilisateur de liker le profil d’un autre utilisateur et retourne un booléen indiquant si un match a été créé.

Paramètres de requête :

id : ID de l'utilisateur effectuant le like

SecondUserid : ID de l'utilisateur liké

Réponse :

```json
{
  "matchCreated": true
}
```

200 OK : Retourne true si un match est créé, sinon false.


## Récupérer les Matchs d’un Utilisateur

URL : /service/getMatches

Méthode : GET

Description : Récupère les matchs d’un utilisateur ainsi que les informations des utilisateurs associés.

Paramètres de requête :

id : ID de l'utilisateur dont on souhaite récupérer les matchs

Réponse :

200 OK : Retourne une liste de matchs avec les informations des utilisateurs correspondants.

## Changer le Statut d’un Match

URL : /service/changeMatchStatus

Méthode : POST

Description : Permet de changer le statut d’un match. Les statuts possibles sont NOT_STARTED, STARTED, et BLOCKED.

Paramètres de requête :

id : ID du match (dans l’URL)
Corps de la requête :

matchStatus : Nouveau statut du match (NOT_STARTED, STARTED, BLOCKED)

ca commence avec not started , et quand ca commence le message ca devient started a toi le front de faire le blocked

### Codes de Statut

200 OK : Requête réussie.

400 Bad Request : Erreur dans la requête, paramètres invalides ou manquants.

404 Not Found : Ressource non trouvée (par exemple, utilisateur inexistant).

500 Internal Server Error : Erreur interne lors du traitement des photos












