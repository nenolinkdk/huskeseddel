# Implementeringsstatus - v1.0

## Implementeret

- Android-projekt med Kotlin, Jetpack Compose og Material 3
- Navigation mellem Varer, Huskeseddel og Indstillinger
- Standardkatalog indlæst fra `default_products.json`
- Kategorier, søgning, fold-ud-sektioner og valg af varer
- Aktiv huskeseddel grupperet efter kategori
- Købt-status, antal, Ryd købte og Nyt indkøb
- Lokal Room-lagring af egne varer, aktiv liste og note
- Tilføjelse, redigering og sletning af egne varer
- Om-, versions- og lokal datalagringstekst
- Deling af aktuel huskeseddel som ren tekst via Android ACTION_SEND (kategorier, varenavne, antal og note)
- Kompakt vareliste på Huskeseddel med uændret checkbox-trykflade
- Købte varer vises med rød tekst uden gennemstregning
- Dobbelttryk på varenavn læser navnet højt med Android TTS (da-DK); mangler dansk stemme uden crash
- APK-navne: `Huskeseddel-debug.apk` (debug) og `Huskeseddel.apk` (release)

## Verifikation

- Unit tests: bestået (`testDebugUnitTest`)
- Debug build: bestået (`assembleDebug`)

## Resterende før release

- Kør Compose UI-tests og de manuelle device-tests fra `TEST_PLAN.md` på emulator/fysisk enhed.
- Generér og dokumentér signeret release-APK/AAB ved egentlig release.
