# SenAnnonces

**Développeur :** Baye Mor Gaye
**Projet :** Application Android de petites annonces — Licence Développement mobile Android (Java)

---

## Fonctionnalités réalisées

### Écran 1 — Liste des annonces
- Affichage des annonces dans une RecyclerView (photo, titre, prix en FCFA, quartier, date)
- Barre de recherche dynamique (paramètre `search=`)
- Ftre par catégorie basé sur GET `/api/categories` (8 catégories avec chips scrollables)
- Tri par prix croissant / décroissant / récent

### Écran 2 — Détail d'une annonce
- Photo en grand, titre, prix, quartier, description complète, nom du vendeur et téléphone
- Bouton « Appeler le vendeur » qui ouvre le composeur téléphonique

### Écran 3 — Connexion / Inscription
- Formulaire de connexion (email + mot de passe)
- Formulaire d'inscription (nom, email, mot de passe, téléphone)
- Token persisté dans SharedPreferences (pas de reconnexion)
- Bouton de déconnexion

### Écran 4 — Publier une annonce
- Accessible uniquement si connecté
- Formulaire : titre, prix, catégorie (liste déroulante depuis l'API), quartier, description
- Envoi via POST `/api/annonces` avec token d'authentification

### Dans toute l'application
- Gestion des 3 états : chargement (ProgressBar), erreur (message + bouton Réessayer), succès
- Messages d'erreur renvoyés par l'API (`error.message`)
- Gestion de l'absence de connexion internet
- Crash handler pour capturer les erreurs inattendues
- Thème sombre avec accents vert acid (#AAFF00)

---

## Contraintes techniques respectées

| Exigence | Implémentation |
|---|---|
| Langage | Java uniquement |
| Réseau | Retrofit 2 + Gson |
| Images | Glide 4.16 |
| Layouts | XML classiques (pas de Compose) |
| Min SDK | API 24 (Android 7.0) |
| Permission | INTERNET dans le manifest |

---

## Compte de démonstration

- **Email :** demo@senannonces.sn
- **Mot de passe :** passer123

---

## Difficultés rencontrées

### 1. OneDrive et le build Gradle
Le projet se trouvant dans un dossier OneDrive, les fichiers de build (`app/build/`) étaient continuellement synchronisés et verrouillés par OneDrive, causant des erreurs `Unable to delete directory` à chaque compilation. Solution : arrêt forcé des daemons Gradle et suppression manuelle du dossier build avant chaque rebuild.

### 2. Types d'IDs inattendus dans l'API
L'API renvoie des IDs de type **String** (UUID pour les annonces/utilisateurs, slugs comme `"telephones"` pour les catégories) au lieu des IDs entiers habituels. Cela a nécessité de retravailler tous les modèles et la logique de filtrage.

### 3. Glide RoundedCorners(0)
L'utilisation de `RoundedCorners(0)` provoquait un crash (`IllegalArgumentException: roundingRadius must be greater than 0`). Corrigé en utilisant `RoundedCorners(16)`.

### 4. Filtrage catégories
L'API attend les slugs de catégories (`telephones`, `informatique`) et non les noms d'affichage (`Téléphones`, `Informatique`). Erreur corrigée lors de la publication d'annonces.

### 5. Communication entre fragments
Le passage de la catégorie sélectionnée depuis `CategoriesFragment` vers `HomeFragment` a nécessité l'utilisation de `FragmentResult API` car les fragments sont recréés à chaque navigation du BottomNavigationView.

### 6. Visibilité du texte dans les filtres
Un double padding (dans le drawable XML ET dans le layout XML) rendait les chips de catégères illisibles. Le Spinner n'avait pas de couleur de texte définie, rendant le texte invisible sur fond sombre.

---

## Fichier APK

L'APK debug est dans le dépôt : [`app/build/outputs/apk/debug/app-debug.apk`](app/build/outputs/apk/debug/app-debug.apk)

Téléchargement direct : https://github.com/GAYENSIS09/SenAnnonces/raw/master/app/build/outputs/apk/debug/app-debug.apk

---

## Structure du projet

```
app/src/main/java/com/example/senannonces/
├── api/
│   ├── ApiClient.java          — Configuration Retrofit + BASE_URL
│   └── ApiService.java         — Interface des endpoints REST
├── adapters/
│   ├── AnnonceAdapter.java     — RecyclerView des annonces
│   ├── CategoryAdapter.java    — Adapter catégories (liste)
│   └── CategoryGridAdapter.java — Adapter catégories (grille)
├── fragments/
│   ├── HomeFragment.java       — Liste des annonces + recherche + filtres
│   ├── CategoriesFragment.java — Grille des catégories
│   └── ProfileFragment.java    — Profil utilisateur + déconnexion
├── models/
│   ├── Annonce.java            — Modèle annonce
│   ├── Category.java           — Modèle catégorie
│   ├── User.java               — Modèle utilisateur
│   ├── AuthResponse.java       — Réponse d'authentification
│   ├── ApiError.java           — Erreur API (code + message)
│   └── ErrorResponse.java      — Wrapper erreur
├── utils/
│   ├── SessionManager.java     — Gestion du token (SharedPreferences)
│   └── NetworkUtils.java       — Vérification connexion internet
├── SenAnnoncesApp.java         — Crash handler global
├── SplashActivity.java         — Écran de démarrage
├── MainActivity.java           — Point d'entrée (redirect splash)
├── LoginActivity.java          — Connexion
├── RegisterActivity.java       — Inscription
├── HomeActivity.java           — Navigation principale (BottomNav)
├── AnnonceDetailActivity.java  — Détail d'une annonce
├── PublishAnnonceActivity.java — Publication d'annonce
└── CrashActivity.java          — Affichage des erreurs critiques
```
