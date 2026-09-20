# Testplan - Huskeseddel v1.0

## Unit tests

- parsing af default_products.json
- søgning i varer
- tilføjelse af custom product
- redigering af custom product
- sletning af custom product
- valg/fravalg af vare
- markering som købt
- "Ryd købte"
- "Nyt indkøb"
- note persisteres
- quantity kan ikke være mindre end 1
- delingstekst indeholder kategorier, varenavne, antal og note

## UI tests

- kategorier kan foldes ud
- vare kan markeres fra kataloget
- valgt vare vises på huskeseddel
- købt vare vises som købt
- søgning filtrerer korrekt
- ny vare kan tilføjes

## Manuel test

- genstart af app bevarer aktiv liste
- genstart bevarer note
- genstart bevarer brugeroprettede varer
- danske tegn vises korrekt
- telefonrotation må ikke give datatab
- app fungerer uden netværk
- deleknap sender aktuel huskeseddel som tekst
- dobbelttryk på varenavn læser navnet højt og ændrer ikke købt-status
- købt vare vises med rød tekst uden gennemstregning

## Release gate

Før release:
- alle unit tests grønne
- centrale UI tests grønne
- debug build fungerer
- release build kan genereres
- signed APK/AAB dokumenteres
