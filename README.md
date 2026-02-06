# Gym Web App (Gestione Palestra)

Applicazione web full-stack per la gestione di una palestra con due ruoli: **ADMIN** e **CLIENTE**.  
Permette di gestire corsi, diete, abbonamenti, iscrizioni, prenotazioni e recensioni, con dashboard dedicata e controllo degli accessi.

## Funzionalita principali
- **Cliente**: registrazione e login, attivazione/cessazione abbonamento, iscrizione ai corsi, prenotazioni dietologo, recensioni corsi/diete (crea, aggiorna, elimina), dashboard personale con stato abbonamento e giorni rimanenti.
- **Admin**: CRUD corsi, diete, abbonamenti; gestione clienti (email, password, ruolo); rimozione abbonamenti, corsi, prenotazioni e recensioni; visualizzazione dashboard cliente.

## Stack tecnologico
- **Frontend**: Angular 21, RxJS
- **Backend**: Spring Boot 4, Spring Security, JDBC
- **Database**: PostgreSQL

## Architettura (in breve)
- **Backend**: REST API su `/api`, sicurezza con sessione + CSRF (cookie), ruoli `ADMIN` / `CLIENTE`, layer `Controller` -> `Service` -> `DAO`.
- **Frontend**: SPA Angular con routing, guard di autenticazione e ruolo, interceptor per cookie/CSRF e gestione 401.

## Requisiti
- Java **17**
- Node.js + npm (versione LTS consigliata)
- PostgreSQL

## Configurazione database
Nel backend e' presente uno **schema SQL** inizializzato automaticamente.
Configurazione di default in `gym_backend/src/main/resources/application.properties`:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/palestra
spring.datasource.username=postgres
spring.datasource.password=0000
```

## Avvio progetto (sviluppo)
### 1) Backend
```
cd gym_backend
./mvnw spring-boot:run
```
Backend disponibile su `http://localhost:8080`.

### 2) Frontend
```
cd gym_frontend
ng serve -o
```
Frontend disponibile su `http://localhost:4200`.

## Credenziali admin (sviluppo)
Nel file `application.properties` sono definite credenziali admin di default:
```
app.admin.email=admin@gym.local
app.admin.password=admin123
```

## Immagini corsi e diete
Le immagini devono essere presenti in:
- `gym_frontend/public/assets/corsi`
- `gym_frontend/public/assets/dietologhi`

Nel pannello admin si inserisce **solo il nome file** (es. `pugilato.png`), che verra' poi risolto automaticamente.

## Principali endpoint API
**Auth**
```
GET /api/auth/csrf
POST /api/auth/register
POST /api/auth/login
GET /api/auth/me
POST /api/auth/logout
```

**Cliente**
```
GET /api/cliente/dashboard
POST /api/cliente/abbonamento
DELETE /api/cliente/abbonamento
POST /api/cliente/corsi/{id}
DELETE /api/cliente/corsi/{id}
POST /api/cliente/prenotazioni/{id}
DELETE /api/cliente/prenotazioni/{id}
POST /api/cliente/corsi/{id}/recensione
PUT /api/cliente/corsi/{id}/recensione
DELETE /api/cliente/corsi/{id}/recensione
POST /api/cliente/diete/{id}/recensione
PUT /api/cliente/diete/{id}/recensione
DELETE /api/cliente/diete/{id}/recensione
PUT /api/cliente/account
```

**Admin**
```
GET /api/admin/clienti
PUT /api/admin/clienti/{id}
GET /api/admin/clienti/{id}/dashboard
DELETE /api/admin/clienti/{id}/abbonamento
DELETE /api/admin/clienti/{id}/corsi/{corsoId}
DELETE /api/admin/clienti/{id}/prenotazioni/{prenId}
DELETE /api/admin/clienti/{id}/recensioni/corsi/{recensioneId}
DELETE /api/admin/clienti/{id}/recensioni/diete/{recensioneId}
DELETE /api/admin/clienti/{id}
```

**Cataloghi**
```
GET /api/abbonamenti
GET /api/corsi
GET /api/diete
```
CRUD completo su questi endpoint e' riservato ad **ADMIN**.

## Struttura del progetto
- `gym_backend/`: Spring Boot, Security, DAO JDBC, schema DB
- `gym_frontend/`: Angular SPA (componenti, servizi, guard, interceptor)


