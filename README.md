# SenAnnonces

**Developpeur :** Baye Mor Gaye
**Projet :** Application Android de petites annonces — Licence Developpement mobile Android (Java)

---

## Fonctionnalites realisees

- [x] Toutes les fonctionnalites demandees dans le sujet sont implementees et fonctionnelles.

### Ecran 1 — Liste des annonces
- Affichage des annonces dans une RecyclerView : photo, titre, prix en FCFA, quartier, date.
- Barre de recherche qui interroge l'API (parametre `search=`).
- Filtre par categorie base sur GET `/api/categories` (8 categories affichees en chips scrollables).
- Tri par prix croissant / decroissant / recent.

### Ecran 2 — Detail d'une annonce
- Photo en grand, titre, prix, quartier, description complete, nom du vendeur et telephone.
- Bouton « Appeler le vendeur » qui ouvre le composeur telephonique avec le numero.

### Ecran 3 — Connexion / Inscription
- Formulaire de connexion (email + mot de passe) et formulaire d'inscription (nom, email, mot de passe, telephone).
- Token stocke dans SharedPreferences : l'utilisateur ne se reconnecte pas a chaque ouverture.
- Bouton de deconnexion (suppression du token).

### Ecran 4 — Publier une annonce
- Accessible uniquement si l'utilisateur est connecte.
- Formulaire : titre, prix, categorie (liste deroulante alimentee par l'API), quartier, description.
- Envoi via POST `/api/annonces` avec le token, puis retour a la liste.

### Dans toute l'application
- Gestion des trois etats de chaque chargement : en cours (ProgressBar), erreur (message + bouton Reessayer), succes.
- Messages d'erreur renvoyes par l'API (champ `error.message`) affiches plutot que des messages generiques.
- L'application ne plante jamais, y compris sans connexion internet.
- Theme sombre avec accents vert acid (#AAFF00).
- URL de base dans une constante unique (`ApiClient.BASE_URL`) modifiable en 5 secondes.

---

## Contraintes techniques respectees

| Exigence | Implementation |
|---|---|
| Langage | Java uniquement (pas de Kotlin) |
| Reseau | Retrofit 2 + Gson |
| Images | Glide 4.16 |
| Layouts | XML classiques (pas de Jetpack Compose) |
| Min SDK | API 24 (Android 7.0) |
| Permission | INTERNET dans le manifeste Android |

---

## Ce qui ne fonctionne pas

- Rien a signaler. Les 4 ecrans sont fonctionnels, la gestion d'erreur est en place, et l'application ne plante pas.

---

## Compte de demonstration

- **Email :** demo@senannonces.sn
- **Mot de passe :** passer123

---

## Difficultes rencontrees

### 1. OneDrive et le build Gradle
Le projet se trouvant initialement dans un dossier OneDrive, les fichiers de build (`app/build/`) etaient verrouilles par la synchronisation OneDrive, causant des erreurs `AccessDeniedException` et `Unable to delete directory`. Solution : deplacer le projet hors de OneDrive (vers `C:\Users\ROG ZEPHYRUS G14\Desktop\ExaMobile`).

### 2. Types d'IDs inattendus dans l'API
L'API renvoie des IDs de type **String** (UUID pour les annonces et utilisateurs, slugs comme `"telephones"` pour les categories) au lieu d'entiers. Cela a necessite d'adapter tous les modeles et la logique de filtrage.

### 3. Glide RoundedCorners(0)
`RoundedCorners(0)` provoque un crash (`IllegalArgumentException: roundingRadius must be greater than 0`). Corrige avec `RoundedCorners(16)`.

### 4. Filtrage par slug de categorie
L'API attend les slugs (`telephones`, `informatique`) et non les noms affiches (`Telephones`, `Informatique`). Corrige lors de la publication et du filtrage.

---

## Fichier APK

L'APK debug se trouve a : `app/build/outputs/apk/debug/app-debug.apk`

---

## Structure du projet

```
app/src/main/java/com/example/senannonces/
├── api/
│   ├── ApiClient.java          — Configuration Retrofit + BASE_URL (constante unique)
│   └── ApiService.java         — Interface des endpoints REST
├── adapters/
│   └── AnnonceAdapter.java     — RecyclerView des annonces
├── models/
│   ├── Annonce.java            — Modele annonce
│   ├── Category.java           — Modele categorie (id String)
│   ├── User.java               — Modele utilisateur
│   ├── AuthResponse.java       — Reponse d'authentification (token)
│   ├── ApiError.java           — Erreur API (code + message)
│   └── ErrorResponse.java      — Wrapper erreur
├── utils/
│   ├── SessionManager.java     — Gestion du token (SharedPreferences)
│   └── NetworkUtils.java       — Verification de connexion internet
├── MainActivity.java           — Point d'entree (redirection vers Splash)
├── SplashActivity.java         — Ecran de demarrage
├── LoginActivity.java          — Connexion
├── RegisterActivity.java       — Inscription
├── HomeActivity.java           — Liste des annonces + recherche + filtre + tri
├── AnnonceDetailActivity.java  — Detail d'une annonce + appel vendeur
└── PublishAnnonceActivity.java — Publication d'une annonce
```
