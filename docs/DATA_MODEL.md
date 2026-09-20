# Datamodel - Huskeseddel v1.0

## Standardkatalog

Standardvarer leveres via JSON med kategorier og varer.

Eksempel:

```json
{
  "categories": [
    {
      "id": "broed",
      "name": "Brød",
      "items": [
        {"id":"franskbroed","name":"Franskbrød"}
      ]
    }
  ]
}
```

## Room-entiteter

### CustomProduct
- id: Long
- name: String
- categoryId: String
- createdAt: Long

### ShoppingItem
- id: Long
- productKey: String
- displayName: String
- categoryId: String
- selected: Boolean
- purchased: Boolean
- quantity: Int = 1

### ShoppingListState
- id: Int = 1
- note: String

## Produktnøgle

Standardvarer bør have en stabil streng-ID, fx:
- milk
- rye_bread_sunflower
- bananas

Egne varer kan bruge fx:
- custom:123

## Regler

- quantity må minimum være 1.
- purchased kan kun være relevant når selected = true.
- "Nyt indkøb" nulstiller selected/purchased og note.
- Egne varer slettes ikke ved "Nyt indkøb".
