java.com.example.beagle.adapter:   questa cartella serve per: 
  - contenere le classi che fanno da ponte tra i dati e la UI di liste o griglie, come RecyclerView o ViewPager.
    
  📌 Cosa fa un Adapter
  
    - Riceve dati grezzi (lista di oggetti, array, cursore DB, ecc.).
    - Crea e riempie le view (righe, card, item) che l’utente vede a schermo.
    - Gestisce riciclo e riuso delle view per migliorare le prestazioni (RecyclerView).

java.com.example.beagle.service:   questa cartella serve per contenere tutte le API

java.com.example.beagle.sourse:   questa cartella serve per:
  - È il livello più vicino alla sorgente dei dati: può essere remota (API, Firebase, Google Sign-In) o locale (database Room, file,                        SharedPreferences).
  - Si occupa solo di recuperare o inviare dati, senza logica di business o UI.
  - Espone metodi “bassi” che il Repository userà per costruire operazioni più complesse.



```mermaid
erDiagram
  USER 1--0+ PET : owns
  USER 1--0+ CHAT : creates
  USER {
    string *userId
    string email UK
  }

  PET {
    string *petId
    string name
    string species
    string race
  }

  CHAT 0+--1 PET : "is about"
  CHAT {
  string *chatId
  string userId FK
  string petId FK
  }

  
