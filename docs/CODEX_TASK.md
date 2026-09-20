# Codex-opgave - Implementer Huskeseddel v1.0

## Mål

Implementer første fungerende Android-version ud fra dokumentationen i repoet.

## Arbejdsform

Arbejd trinvis og commit efter naturlige milepæle.

Undgå at udvide scope udenfor v1.

## Fase 1 - Projektgrundlag

- opret standard Android-projekt
- Kotlin
- Jetpack Compose
- Material 3
- minSdk på et rimeligt moderne niveau
- package: com.nenolink.huskeseddel
- versionsnavn: 1.0.0-dev
- tilføj navigation mellem Varer, Huskeseddel og Indstillinger

## Fase 2 - Data

- implementer JSON-modeller
- indlæs app/src/main/assets/default_products.json
- implementer Room
- implementer custom products
- implementer active shopping list
- implementer note persistence

## Fase 3 - Varer-skærm

- kategorier
- expandable sections
- checkboxes
- søgning
- tilføj vare
- rediger/slet egne varer

## Fase 4 - Huskeseddel

- vis valgte varer grupperet pr. kategori
- marker som købt
- quantity
- note
- Ryd købte
- Nyt indkøb

## Fase 5 - Indstillinger

- Om appen
- version
- Nenolink
- tekst om lokal datalagring

## Fase 6 - Test

Implementer unit tests og relevante Compose UI tests jf. docs/TEST_PLAN.md.

## Begrænsninger

Implementer IKKE:
- login
- cloud
- deling
- betaling
- tracking
- analytics
- reklamer
- notifikationer
- netværkskald

## Definition of done

- appen bygger
- testene kører
- standardvarer indlæses
- custom varer fungerer
- aktiv liste overlever genstart
- note overlever genstart
- release-noter/status dokumenteres i docs/IMPLEMENTATION_STATUS.md

## Afslutning

Når implementeringen er færdig:
1. kør testene
2. kør build
3. dokumenter resultatet
4. commit alle ændringer
5. push branch
6. merge ikke til main uden udtrykkelig besked
