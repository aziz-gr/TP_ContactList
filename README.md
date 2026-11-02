# 📱 Application Liste de Contacts (TP Android V2)

[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Android SDK](https://img.shields.io/badge/SDK-24%2B-green.svg)](https://developer.android.com/)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-blue.svg)](https://m3.material.io/)

Application Android native développée en **Kotlin** avec des **interfaces XML** et des composants **Material Design 3**. Ce projet permet de gérer une liste de contacts avec consultation, modification, appels téléphoniques, envoi d'e-mails et sélection/prise de photos.

---

## 🚀 Fonctionnalités Principales

### 1. Écran Principal (`MainActivity`)
- **Affichage dynamique** : Liste personnalisée (`ListView`) présentant pour chaque contact sa photo, son prénom, son nom et son numéro de téléphone.
- **Compteur de contacts** : Affichage dynamique du nombre de contacts disponibles dans la liste.
- **Ouverture par Intent explicite** : Un clic sur un contact transmet les données sous forme d'objet `Parcelable` à l'écran de détail (`DetailActivity`).
- **Mise à jour en direct** : Utilisation de `ActivityResultContracts.StartActivityForResult()` pour mettre à jour immédiatement la liste et la photo de profil dès l'enregistrement des modifications.

### 2. Écran de Détail (`DetailActivity`)
- **Consultation & Édition** : Visualisation et modification du nom, prénom, numéro de téléphone et adresse e-mail.
- **Appel Téléphonique (`Intent` implicite)** : Bouton **« Appeler »** ouvrant le composeur téléphonique prérempli avec le numéro (`Intent.ACTION_DIAL`).
- **Envoi d'E-mail (`Intent` implicite)** : Bouton **« Envoyer un e-mail »** ouvrant l'application de messagerie avec l'adresse préremplie (`Intent.ACTION_SENDTO`).
- **Photo depuis la Galerie** : Bouton **« Galerie »** permettant de choisir une photo (`ActivityResultContracts.GetContent()`), qui est sauvegardée de façon permanente dans le stockage interne de l'application (`filesDir`).
- **Photo depuis la Caméra** : Bouton **« Appareil photo »** ouvrant la caméra du téléphone via `FileProvider` (`ActivityResultContracts.TakePicture()`) avec gestion dynamique des permissions de l'appareil photo (`Manifest.permission.CAMERA`).
- **Enregistrer** : Validation des champs obligatoires (Nom, Prénom, Téléphone) avant de renvoyer le résultat (`RESULT_OK`) à `MainActivity`.
- **Annuler** : Annulation des modifications sans enregistrement (`RESULT_CANCELED`).

---

## 🛡️ Gestion des Cas Limites & Sécurité

- **Validation des saisies** : Affichage de messages d'erreur (`TextInputLayout.error`) et de `Toast` si les champs obligatoires (Nom, Prénom, Téléphone) sont vides.
- **Indisponibilité des applications** : Gestion des exceptions (`ActivityNotFoundException`) si aucune application de téléphonie ou de messagerie n'est installée.
- **Annulation des photos** : Message d'information si l'utilisateur annule la sélection dans la galerie ou la capture avec l'appareil photo.
- **Gestion des permissions** : Demande dynamique de la permission `CAMERA` au moment de la prise de vue avec message d'explication si la permission est refusée.

---

## 📁 Architecture du Projet

```text
com.example.contactlist/
├── Contact.kt             # Modèle de données parcelable représentant un contact
├── ContactAdapter.kt      # Adaptateur personnalisé pour la ListView
├── DetailActivity.kt      # Activité de consultation, édition et actions de contact
├── ImageUtils.kt          # Utilitaire de gestion des photos (stockage interne & FileProvider)
└── MainActivity.kt        # Activité principale affichant la liste des contacts
```

### Layouts XML
- `res/layout/activity_main.xml` : Interface principale avec Toolbar, compteur et `ListView`.
- `res/layout/activity_detail.xml` : Interface de détail avec cartes Material, champs de texte et boutons d'action.
- `res/layout/item_contact.xml` : Design d'un élément de la liste de contacts.
- `res/xml/file_paths.xml` : Configuration des chemins d'accès pour `FileProvider`.

---

## 🛠️ Configuration Technique

- **Langage** : Kotlin
- **Min SDK** : 24 (Android 7.0)
- **Target SDK / Compile SDK** : Android API 35 / 37
- **Bibliothèques principales** :
  - `androidx.appcompat`
  - `androidx.activity-ktx`
  - `com.google.android.material:material`
  - `androidx.constraintlayout`
- **Architecture UI** : View Binding XML

---

## 💻 Installation et Exécution

1. Cloner ce dépôt GitHub :
   ```bash
   git clone <URL_DU_DEPOT>
   ```
2. Ouvrir le projet dans **Android Studio**.
3. Laisser Gradle synchroniser les dépendances.
4. Lancer l'application sur un émulateur ou un appareil Android connecté.

---

## 📝 Auteur
Travail réalisé individuellement dans le cadre du TP Android Liste de Contacts (Version 2).
