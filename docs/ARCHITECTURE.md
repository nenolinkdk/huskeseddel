# Arkitektur - Huskeseddel v1.0

## Anbefalet teknisk stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Room til brugerdata og aktiv liste
- DataStore til små indstillinger
- JSON asset til standardvarekatalog

## Lag

### UI
Compose-skærme og komponenter.

### ViewModel
Holder UI-state og kalder repositories.

### Repository
Abstraktion over Room og indlæsning af standarddata.

### Data
- Room-database
- JSON asset
- DataStore preferences

## Forslag til pakker

com.nenolink.huskeseddel

- data
  - local
  - model
  - repository
- domain
- ui
  - products
  - shoppinglist
  - settings
  - components
- util

## Vigtige designvalg

Standardvarelisten skal ikke behandles som brugerdata. Den skal kunne opdateres eller lokaliseres senere uden at ødelægge brugerens egne varer.

Brugerens egne varer og aktive indkøb gemmes i Room.

## Fremtidssikring

Arkitekturen skal kunne udvides med:
- flere sprog
- favoritter
- flere gemte lister
- deling
- eksport
- premium-funktioner

Disse funktioner skal ikke implementeres i v1.
