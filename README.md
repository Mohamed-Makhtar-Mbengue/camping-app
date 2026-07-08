# 🏕️ Camping PMS — Domaine de Léveno

Application de gestion de camping (Property Management System) complète, développée avec **Spring Boot 4** (backend) et **Angular 21** (frontend).

---

## 📋 Table des matières

- [Architecture](#architecture)
- [Fonctionnalités](#fonctionnalités)
- [Stack technique](#stack-technique)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Structure du projet](#structure-du-projet)

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│              Client (Navigateur)                │
│  ┌──────────────────┐  ┌──────────────────────┐ │
│  │   Site Public    │  │   Espace Admin       │ │
│  │  (Visiteurs)     │  │  (Personnel camping) │ │
│  └────────┬─────────┘  └──────────┬───────────┘ │
└───────────┼────────────────────────┼─────────────┘
            │ HTTP/REST              │ HTTP/REST + JWT
            ▼                        ▼
┌─────────────────────────────────────────────────┐
│           Spring Boot 4 Backend                 │
│  ┌──────────┐ ┌──────────┐ ┌─────────────────┐ │
│  │  Public  │ │   Auth   │ │   Admin API     │ │
│  │   API    │ │   JWT    │ │  (CRUD complet) │ │
│  └──────────┘ └──────────┘ └─────────────────┘ │
│  ┌──────────┐ ┌──────────┐ ┌─────────────────┐ │
│  │ Pricing  │ │  Email   │ │     Stripe      │ │
│  │  ACSI   │ │  + PDF   │ │    Paiement     │ │
│  └──────────┘ └──────────┘ └─────────────────┘ │
└────────────────────┬────────────────────────────┘
                     │ JDBC
                     ▼
┌─────────────────────────────────────────────────┐
│         Supabase PostgreSQL 17                  │
│  accommodations │ bookings │ customers          │
│  pricing_seasons │ acsi_periods                 │
└─────────────────────────────────────────────────┘
```

---

## ✨ Fonctionnalités

### 🌐 Site Public (Visiteurs)
- Page d'accueil avec carousel d'images (piscine, restaurant, spa, sport...)
- Liste paginée des hébergements (200 mobil-homes + 75 emplacements)
- Page détail d'un hébergement avec photos et équipements
- Wizard de réservation en 3 étapes :
  - Dates + vérification disponibilité
  - Informations personnelles
  - Paiement simulé (Stripe)
- Réduction **CampingCard ACSI** automatique selon les périodes
- PDF bon d'échange téléchargeable après réservation
- Page de confirmation avec récapitulatif
- **Multi-langues** : 🇫🇷 Français / 🇬🇧 English / 🇩🇪 Deutsch / 🇳🇱 Nederlands

### 🔐 Espace Admin
- Authentification JWT (access token 15min + refresh token 7 jours)
- Dashboard avec statistiques (hébergements, réservations, revenus)
- Gestion CRUD des hébergements
- Gestion des réservations avec :
  - **Assignation d'emplacement** — suggestions par catégorie ET capacité
  - Confirmation + envoi email automatique avec bon d'échange PDF
  - Gestion de la **caution** (encaisser, rembourser, retenue partielle)
  - Mise à jour des statuts (PENDING / CONFIRMED / CANCELLED)
- Mode sombre / clair

### 📧 Email & PDF
- Email HTML automatique à la confirmation admin
- **Bon d'échange PDF** avec :
  - Informations client complètes
  - Détails du séjour
  - Véhicule et animaux
  - Tarification détaillée
  - Règlement intérieur du camping

### 💰 Tarification
- Tarifs saisonniers dynamiques par hébergement
- Réduction CampingCard ACSI selon les périodes définies
- Caution configurable par hébergement

---

## 🛠️ Stack technique

### Backend
| Technologie | Version | Usage |
|-------------|---------|-------|
| Java | 17 | Langage |
| Spring Boot | 4.0.6 | Framework principal |
| Spring Security | 7.x | Auth + JWT |
| Spring Data JPA | 3.x | ORM |
| Hibernate | 7.x | ORM JPA |
| PostgreSQL | 17.6 | Base de données |
| Supabase | - | DBaaS |
| iText 7 | 8.0.3 | Génération PDF |
| JavaMailSender | - | Envoi emails |
| Stripe Java SDK | 27.2.0 | Paiement |
| Lombok | - | Réduction boilerplate |
| JUnit 5 + Mockito | - | Tests unitaires |

### Frontend
| Technologie | Version | Usage |
|-------------|---------|-------|
| Angular | 21.2 | Framework |
| Angular Material | 21.2 | UI Components |
| TypeScript | 5.9 | Langage |
| RxJS | 7.8 | Programmation réactive |
| ngx-translate | 16+ | Internationalisation |
| SCSS | - | Styles |

---

## 🚀 Installation

### Prérequis
- Java 17+
- Node.js 20+
- npm 10+
- Compte Supabase (PostgreSQL)
- Compte Stripe (pour le paiement)
- Compte Gmail (pour les emails)

### Backend

```bash
# Clone le projet
git clone https://github.com/Mohamed-Makhtar-Mbengue/camping-app.git
cd camping-app

# Configure les variables d'environnement
cp src/main/resources/application.properties src/main/resources/application-local.properties
# Édite application-local.properties avec tes valeurs

# Lance le backend
./mvnw spring-boot:run
```

### Frontend

```bash
# Clone le projet frontend
git clone https://github.com/Mohamed-Makhtar-Mbengue/camping-app-front.git
cd camping-app-front

# Installe les dépendances
npm install

# Lance le frontend
ng serve
```

L'application sera accessible sur :
- **Frontend** : http://localhost:4200
- **Backend API** : http://localhost:8080

---

## ⚙️ Configuration

### `application-local.properties`

```properties
# Base de données Supabase
spring.datasource.url=jdbc:postgresql://db.VOTRE_ID.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=VOTRE_MOT_DE_PASSE

# JWT
jwt.secret=VOTRE_SECRET_BASE64

# Email Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=VOTRE_EMAIL@gmail.com
spring.mail.password=VOTRE_APP_PASSWORD

# Stripe
stripe.secret.key=sk_test_VOTRE_CLE_SECRETE
stripe.public.key=pk_test_VOTRE_CLE_PUBLIQUE
stripe.webhook.secret=whsec_VOTRE_WEBHOOK_SECRET
```

### Variables d'environnement en production

```bash
export DB_URL=jdbc:postgresql://...
export DB_USERNAME=postgres
export DB_PASSWORD=...
export JWT_SECRET=...
export STRIPE_SECRET_KEY=sk_live_...
export STRIPE_PUBLIC_KEY=pk_live_...
```

---

## 📡 API Documentation

### Auth
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/register` | Inscription |
| POST | `/auth/login` | Connexion |
| POST | `/auth/logout` | Déconnexion |
| POST | `/auth/refresh` | Rafraîchir le token |
| GET | `/auth/me` | Utilisateur connecté |

### Public (sans authentification)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/public/accommodations` | Liste des hébergements |
| GET | `/public/accommodations/{id}` | Détail d'un hébergement |
| GET | `/public/accommodations/{id}/availability` | Vérifier disponibilité |
| POST | `/public/bookings` | Créer une réservation |
| GET | `/public/bookings/{id}/pdf` | Télécharger le bon d'échange |
| GET | `/public/acsi/check` | Vérifier éligibilité ACSI |
| POST | `/public/payment/create-intent` | Créer un PaymentIntent Stripe |

### Admin (authentification requise)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/accommodations` | Liste paginée |
| POST | `/api/accommodations` | Créer un hébergement |
| PUT | `/api/accommodations/{id}` | Modifier |
| DELETE | `/api/accommodations/{id}` | Supprimer |
| GET | `/api/bookings` | Toutes les réservations |
| GET | `/api/bookings/my` | Mes réservations |
| PATCH | `/api/bookings/{id}/status` | Changer le statut |
| GET | `/api/bookings/{id}/assignment/suggestions` | Suggestions d'emplacement |
| PATCH | `/api/bookings/{id}/assignment/assign` | Assigner et confirmer |
| PATCH | `/api/bookings/{id}/deposit/hold` | Encaisser la caution |
| PATCH | `/api/bookings/{id}/deposit/return` | Rembourser la caution |
| PATCH | `/api/bookings/{id}/deposit/partial-retain` | Retenue partielle |
| GET | `/api/stats` | Statistiques globales |
| GET | `/api/stats/by-accommodation` | Stats par hébergement |

---

## 📁 Structure du projet

### Backend
```
src/main/java/com/camping/pms/
├── auth/                    # Login, Register, Refresh
├── accommodations/          # Hébergements + tarification
│   ├── AcsiPeriod.java
│   ├── AcsiService.java
│   ├── PricingSeason.java
│   ├── PricingService.java
│   └── dto/
├── bookings/                # Réservations + caution + assignation
│   ├── AssignmentController.java
│   ├── DepositController.java
│   └── dto/
├── customers/               # Clients
├── config/                  # CORS, Security, Stripe
├── email/                   # EmailService + PdfService
├── payment/                 # Stripe PaymentService
├── public_api/              # Endpoints publics
├── security/                # JWT Filter, Service
└── stats/                   # Statistiques
```

### Frontend
```
src/app/
├── core/
│   ├── services/            # auth, accommodation, booking, public, language, theme
│   ├── guards/              # auth-guard, admin-guard
│   └── interceptors/        # jwt-interceptor
├── features/
│   ├── auth/                # login, register
│   ├── dashboard/           # Tableau de bord admin
│   ├── accommodations/      # Liste + formulaire admin
│   ├── bookings/            # Liste + formulaire + dialog assignation
│   │   └── assignment-dialog/
│   ├── profile/             # Profil utilisateur
│   └── public/              # Site public
│       ├── home/            # Page d'accueil + carousel
│       ├── accommodation-detail/
│       ├── booking-wizard/  # Wizard réservation 3 étapes
│       └── confirmation/    # Page confirmation + PDF
└── shared/
    └── components/
        ├── navbar/          # Navbar admin
        └── public-navbar/   # Navbar publique + sélecteur langue
src/assets/
└── i18n/                    # Fichiers de traduction
    ├── fr.json
    ├── en.json
    ├── de.json
    └── nl.json
```

---

## 🏕️ Nomenclature des hébergements

| Bloc | Catégorie | Capacité | Nombre |
|------|-----------|----------|--------|
| MH-A à MH-D | STANDARD | 4-6 pers. | 60 |
| MH-E à MH-H | CONFORT | 4-8 pers. | 60 |
| MH-I à MH-J | PREMIUM PLUS | 4-8 pers. | 30 |
| MH-K | PREMIUM (TRIBU) | 12 pers. | 10 |
| MH-L à MH-M | VIP SPA | 6-8 pers. | 20 |
| MH-N | EXCLUSIF | 6-8 pers. | 15 |
| MH-O | INSOLITE | 4-6 pers. | 5 |
| EMP-A à EMP-C | GRAND CONFORT | 2 pers. | 40 |
| EMP-D à EMP-F | PREMIUM EMPLACEMENT | 2 pers. | 35 |

---

## 🧪 Tests

```bash
# Lance les tests unitaires
./mvnw test

# Résultats attendus : 10 tests, 0 échecs
# - JwtServiceTest (5 tests)
# - AuthControllerTest (4 tests)
# - PmsApplicationTests (1 test)
```

---

## 📄 Licence

Ce projet est développé pour le **Domaine de Léveno Camping Village ⭐⭐⭐⭐**.

---

## 👨‍💻 Auteur

**Mohamed Makhtar Mbengue**
- GitHub: [@Mohamed-Makhtar-Mbengue](https://github.com/Mohamed-Makhtar-Mbengue)
