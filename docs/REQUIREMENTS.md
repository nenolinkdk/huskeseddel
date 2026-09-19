# Kravspecifikation - Huskeseddel v1.0

## Mål

At bygge en lille, stabil Android-app til indkøbslister med fokus på enkel betjening, offline-brug og lav kompleksitet.

## Primær brugerrejse

1. Brugeren åbner appen.
2. Brugeren vælger varer via afkrydsning.
3. Valgte varer vises på en huskeseddel.
4. Brugeren kan skrive en kort note.
5. Under indkøb markeres varer som købt.
6. Købte varer kan ryddes.
7. Et nyt indkøb kan starte uden at standardvarekataloget ændres.

## Funktionelle krav

### Varekatalog
- Varer vises opdelt i kategorier.
- Kategorier kan foldes ud og sammen.
- Der skal være søgning på tværs af varer.
- Standardvarer leveres fra JSON.
- Brugeren kan tilføje egne varer.
- Egne varer kan redigeres og slettes.

### Aktuel huskeseddel
- En vare kan vælges til indkøb.
- Valgte varer vises separat på huskesedlen.
- Huskesedlen bevarer kategoristruktur.
- En vare kan markeres som købt uden straks at forsvinde.
- Funktionen "Ryd købte" fjerner købte varer fra den aktive liste.
- Funktionen "Nyt indkøb" nulstiller den aktive liste.

### Note
- Den aktive huskeseddel kan have én kort fritekstnote.
- Noten gemmes lokalt og bevares ved genstart.

### Data
- Appen skal fungere helt offline.
- Ingen konto, login eller cloud-backend i v1.
- Standardvarer ligger i JSON.
- Brugerdata og aktiv liste lagres lokalt.

## Ikke med i v1
- Delte lister mellem brugere
- Cloud-synkronisering
- Webapp
- Betaling i appen
- Push-notifikationer
- Barcode-scanning
- Butiksspecifik sortering

## Kvalitetskrav
- Ingen unødvendige Android-tilladelser.
- Appen må ikke miste data ved normal lukning/genstart.
- Danske specialtegn skal fungere korrekt.
- UI skal fungere på almindelige Android-telefoner.
- Arkitekturen skal tillade senere oversættelse til flere sprog.
