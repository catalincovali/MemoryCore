# Spiegazione dettagliata di `GameScreen.kt`

Analisi della versione corrente di `app/src/main/java/com/catalincovali/memorycore/GameScreen.kt`,
con spiegazione di come funzionano i vari oggetti Compose, dei problemi presenti e di
come risolverli concettualmente. **Nessuna modifica al codice.**

---

## 1. La struttura generale: cos'è un `@Composable`

Un `@Composable` è una funzione che **descrive** un pezzo di UI. Non disegna nulla
direttamente: quando Compose la chiama, costruisce un albero di nodi e li ridisegna
ogni volta che lo **stato** cambia (questo si chiama *recomposition*).

Tre cose fondamentali da capire:

1. **Le composable possono solo essere chiamate da altre composable.** Per questo
   `MainActivity.onCreate` usa `setContent { ... }`: dentro a quel blocco "entri" nel
   mondo Compose.
2. **Una composable non ha un `return` UI: emette UI nell'ordine in cui chiami le
   funzioni.** `Column { A(); B(); C() }` mette A, B, C in colonna nell'ordine.
3. **I `Modifier` sono il modo per modificare un nodo dall'esterno** (dimensioni,
   padding, click, sfondo, bordo...). Sono *immutabili* e si concatenano: ogni
   `.qualcosa(...)` restituisce un nuovo Modifier.

---

## 2. `Modifier` — come si leggono le tue catene

```kotlin
Modifier
    .weight(1f)
    .fillMaxHeight()
    .clip(RoundedCornerShape(12.dp))
    .background(color)
    .border(...)
    .clickable { ... }
```

**L'ordine conta.** Compose applica i modifier da sinistra a destra:

- `weight(1f)` — funziona **solo** dentro un `Row` o `Column`. Dice: "prendi una
  frazione dello spazio disponibile". Se due figli hanno `weight(1f)` ognuno prende
  metà; con `weight(0.5f)` e `weight(0.5f)` è lo stesso (i pesi sono *relativi*).
- `fillMaxHeight()` / `fillMaxWidth()` / `fillMaxSize()` — "occupa tutta
  l'altezza/larghezza/entrambe del genitore".
- `clip(RoundedCornerShape(12.dp))` — ritaglia ciò che viene **dopo** (sfondo e
  bordo) con angoli arrotondati. Se metti `clip` dopo `background`, lo sfondo non
  viene arrotondato!
- `background(color)` — colora dietro al contenuto.
- `border(...)` — disegna un bordo.
- `clickable { ... }` — rende il nodo cliccabile e gestisce il ripple.

> Regola pratica: **prima dimensioni** (`size`/`weight`/`fillMax*`), **poi
> forma/clip**, **poi sfondo/bordo**, **poi click/padding interno**.

---

## 3. `Row`, `Column`, `Box` — i mattoni del layout

- **`Column`** mette i figli uno sotto l'altro.
- **`Row`** li mette uno accanto all'altro.
- **`Box`** li sovrappone (utile per centrare con
  `contentAlignment = Alignment.Center`).

I parametri `verticalArrangement` / `horizontalArrangement` decidono come distribuire
lo **spazio extra** tra i figli:
- `Arrangement.spacedBy(8.dp)` — mette 8.dp di spazio tra ogni figlio.
- `Arrangement.Center` — accumula i figli al centro.

`verticalAlignment` / `horizontalAlignment` decidono come allineare i figli sull'asse
trasversale.

---

## 4. Lo stato in Compose — il pezzo più importante

Hai scritto:

```kotlin
var sequenceString by rememberSaveable { mutableStateOf("") }
```

Decomposto:

- **`mutableStateOf("")`** crea un *contenitore osservabile* con dentro `""`. È un
  `MutableState<String>`. Quando il valore cambia, Compose se ne accorge e
  **ricompone** tutte le funzioni che lo leggono.
- **`remember { ... }`** dice a Compose: "calcola questa cosa la prima volta, poi
  tienila in memoria attraverso le ricomposizioni". Senza `remember`, ogni
  recomposition creerebbe un nuovo `MutableState` vuoto e perderesti il valore.
- **`rememberSaveable`** è come `remember` ma **sopravvive anche alla rotazione
  dello schermo** (e al process death). Per questo l'hai scelto: la consegna lo
  richiede esplicitamente.
- **`by`** è la *property delegation* di Kotlin. Senza `by` dovresti scrivere
  `sequenceString.value` ogni volta. Con `by` (più gli import `getValue`/`setValue`
  che hai aggiunto) puoi trattarla come una normale variabile: leggi
  `sequenceString`, scrivi `sequenceString = "..."`.

**Problema 1**: hai dichiarato `sequenceString` ma **non la usi mai**. Né dentro
`clickable`, né la passi a `SequenceText`, né la modifichi quando si preme un
colore. È stato dichiarato e basta.

### Quale tipo conviene?

Hai scelto `String`, ma la consegna dice "lettere separate da virgole". Hai due
opzioni:

- **Opzione A — `List<String>`** (più pulita): la sequenza è una lista, e produci
  la stringa solo quando la mostri (`sequence.joinToString(", ")`). Permette anche
  di sapere subito *quanti* elementi ci sono (Schermata 2).
- **Opzione B — `String`**: più semplice da salvare ma poi ogni volta che vuoi
  sapere la lunghezza devi fare `split(", ").size`, e per cancellare l'ultimo devi
  parsare. Sconsigliato.

Se scegli `List<String>`, `rememberSaveable` funziona di default perché
`List<String>` è `Parcelable`/serializzabile. Se in futuro userai un enum
(`GameColor.RED`, `GameColor.GREEN`...) dovrai imparare i **`Saver`** custom — ma
per ora la lista di stringhe basta.

---

## 5. State hoisting (sollevamento dello stato) — il pattern fondamentale

Lo stato vive **in un solo posto**, di solito nella composable più "in alto" che
ne ha bisogno. I figli ricevono:

1. **Lo stato** come parametro normale (`sequence: List<String>`).
2. **Le azioni** come parametri-lambda (`onColorClick: (String) -> Unit`).

Il figlio non sa cosa succede quando viene cliccato: chiama la lambda, e il
genitore decide.

Schema mentale (UDF — Unidirectional Data Flow):

```
        ┌──── stato ────┐
        ▼               │
     figlio          genitore
        │               ▲
        └─── evento ────┘
```

Nel tuo codice **`GameScreen`** è il genitore. Deve:
- Tenere lo stato `sequence`.
- Passare `sequence` a `SequenceText`.
- Passare a `ColorGrid` la lambda
  `onColorClick = { letter -> sequence = sequence + letter }`.
- Passare a `ActionButtons`
  `onClear = { sequence = emptyList() }` e
  `onEndGame = { ... salva e naviga ... }`.

---

## 6. Analisi pezzo per pezzo del file

### `GameScreen` (righe 28-101)

```kotlin
val colors = listOf("R" to ColorRed, ...)
```
Crea una lista di `Pair<String, Color>`. Va bene, ma sarà ricreata a ogni
recomposition; è economico, però il pattern "pulito" è metterla `private val`
fuori dalla funzione perché è una costante.

```kotlin
var sequenceString by rememberSaveable { mutableStateOf("") }
```
- Sintassi corretta.
- Non viene usata.

```kotlin
val isLandscape = LocalConfiguration.current.orientation == ...
```
`LocalConfiguration.current` è un **`CompositionLocal`**: un valore "ambientale"
che le composable possono leggere senza riceverlo come parametro. Quando
l'orientamento cambia, la composable viene ricomposta automaticamente, quindi
`isLandscape` si aggiorna.

```kotlin
if (isLandscape) { Row { ColorGrid(...); Column { SequenceText(...); ActionButtons(...) } } }
else            { Column { ColorGrid(...); SequenceText(...); ActionButtons(...) } }
```
La struttura ricalca correttamente la consegna (3×2 a sinistra in landscape,
tutto incolonnato in portrait).

**Problema 2**: in entrambi i rami chiami `SequenceText(modifier = ...)` senza il
parametro `sequence`, ma l'hai reso obbligatorio nella dichiarazione (riga 148).
**Non compila.**

**Problema 3**: chiami `ColorGrid(colors, modifier = ...)` senza passare
`onColorClick`, ma l'hai reso obbligatorio (riga 107). **Non compila.**

### `ColorGrid` (righe 103-143)

```kotlin
fun ColorGrid(
    colors: List<Pair<String, Color>>,
    modifier: Modifier = Modifier,
    onColorClick: (String) -> (Unit)
)
```
La sintassi `(String) -> (Unit)` è insolita: le parentesi attorno a `Unit` sono
superflue ma legali. Lo standard è `(String) -> Unit`.

**Convenzione Compose violata**: i parametri obbligatori vanno **prima** di
quelli con default. `modifier` ha default, `onColorClick` no → metti
`onColorClick` prima di `modifier`, oppure dagli un default
`onColorClick: (String) -> Unit = {}`.

```kotlin
colors.chunked(2).forEach { row -> Row { row.forEach { (letter, color) -> Box(...) } } }
```
`chunked(2)` divide la lista in `[[R,G],[B,M],[Y,C]]` — perfetto per fare 3
righe da 2.

```kotlin
Row(
    modifier = Modifier.fillMaxSize().weight(1f),
    ...
)
```
**Sottile**: `fillMaxSize()` su un `Row` dentro un `Column` con `weight(1f)` è
ridondante. `weight(1f)` già divide l'altezza disponibile in parti uguali (3
righe → ognuna 1/3). `fillMaxSize` dice "prendi tutto" e poi `weight` la corregge
a 1/3 dell'altezza. Funziona, ma **basterebbe `Modifier.fillMaxWidth().weight(1f)`**
ed è più leggibile.

```kotlin
Box(
    modifier = Modifier
        .weight(1f).fillMaxHeight()
        .clip(...).background(color).border(...)
    .clickable { onColorClick(letter) },
    contentAlignment = Alignment.Center
) { Text(letter, color = Color.White) }
```
- Il `clickable` ora c'è.
- Chiama la callback con la lettera.
- **Indentazione**: `.clickable` è scritta a indentazione "fuori" dalla catena —
  Kotlin se ne frega ma per leggibilità mettila in linea con gli altri
  `.something`.

### `SequenceText` (righe 145-157)

```kotlin
fun SequenceText(
    modifier: Modifier = Modifier,
    sequence: List<String>
)
```
Stesso problema di convenzione: parametro obbligatorio dopo uno con default.

```kotlin
Text(
    text = sequence,
    ...
)
```
**Problema 4 — Errore di compilazione grosso**: `Text` accetta `text: String` (o
`AnnotatedString`), **non** `List<String>`. Devi convertire: la traduzione
idiomatica è `text = sequence.joinToString(", ")`.

Inoltre, secondo la consegna, se la sequenza è vuota va comunque mostrata in
qualche modo (anche solo una stringa vuota — basta che il widget non sparisca).

### `ActionButtons` (righe 159-175)

```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    ...
)
```
**Problema 5**: stai ignorando il parametro `modifier` ricevuto. Il chiamante ti
passa `Modifier.weight(1.5f).fillMaxWidth()` ma tu lo butti via e usi
`Modifier.fillMaxWidth()` da zero. Di conseguenza, il **`weight(1.5f)` non ha
effetto** e i due bottoni in portrait si compattano in alto invece di occupare
la fascia che gli avevi destinato.

Pattern corretto: `Row(modifier = modifier.fillMaxWidth(), ...)` — parti dal
modifier ricevuto e ci aggiungi quello che ti serve.

```kotlin
Button(onClick = onClear) { Text("Clear") }
Button(onClick = onEndGame) { Text("End game") }
```
- Le lambda sono cablate.
- "Clear" e "End game" sono **stringhe hardcoded**: per la localizzazione IT/EN
  dovrai usare `stringResource(R.string.clear)`.

**Problema 6**: in `GameScreen` (righe 69-70 e 95-96) le passi `onClear = { }` e
`onEndGame = {}` — lambda **vuote**. Non succede niente quando si clicca. Devono
modificare lo stato della sequenza.

### Le `@Preview` (righe 177-191)

```kotlin
fun GameScreenPreview() { MemoryCoreTheme { GameScreen() } }
```
Funzionerebbero se `GameScreen()` non avesse problemi di compilazione. Una volta
sistemato tutto, le preview ti permettono di vedere portrait e landscape senza
avviare l'emulatore.

---

## 7. Riassunto dei problemi e come risolverli (concettualmente)

| # | Problema | File:riga | Soluzione concettuale |
|---|----------|-----------|----------------------|
| 1 | `sequenceString` dichiarato ma mai usato | 40 | Usalo come fonte di verità: passalo a `SequenceText`, modificalo in `onColorClick`, `onClear`, `onEndGame`. |
| 2 | Chiami `SequenceText(...)` senza il parametro obbligatorio `sequence` | 60, 86 | Passa lo stato: `sequence = sequence` (usando una `List<String>`). |
| 3 | Chiami `ColorGrid(...)` senza `onColorClick` | 49, 80 | Passa la lambda: `onColorClick = { letter -> sequence = sequence + letter }`. |
| 4 | `Text(text = sequence)` con `sequence: List<String>` | 152 | `text = sequence.joinToString(", ")`. |
| 5 | `ActionButtons` ignora il `modifier` ricevuto | 166 | Sostituisci `Modifier.fillMaxWidth()` con `modifier.fillMaxWidth()`. |
| 6 | `onClear` e `onEndGame` lambda vuote | 69-70, 95-96 | `onClear = { sequence = emptyList() }`; `onEndGame = { salva su lista partite, svuota, naviga }`. |
| 7 | Parametri obbligatori dopo parametri con default | 105-107, 147-148 | Riordina: prima i required, poi `modifier`, oppure dai un default. |
| 8 | `Modifier.fillMaxSize().weight(1f)` ridondante | 115-117 | `Modifier.fillMaxWidth().weight(1f)` basta. |
| 9 | "Clear" / "End game" hardcoded | 172, 173 | `stringResource(R.string.clear)` + crea `values-it/strings.xml`. |
| 10 | Niente click sulle azioni di "fine partita" verso Schermata 2 | 70, 96 | Passa anche `navController` (o un'altra lambda `onNavigateToList`) dal genitore. |

---

## 8. Come riflettere sull'architettura prima di scrivere altro codice

Prima di toccare GameScreen di nuovo, decidi:

1. **Dove vive `sequence`?** Per ora dentro `GameScreen` con `rememberSaveable` è
   sufficiente per la rotazione. Quando aggiungerai la lista delle partite, dovrà
   vivere più in alto (o in un `ViewModel`) perché serve a entrambe le schermate.
2. **Come navigo a Schermata 2 da `onEndGame`?** `GameScreen` deve ricevere dal
   `MainActivity` una lambda `onEndGame: (List<String>) -> Unit` che fa due cose:
   aggiunge la partita alla lista globale e chiama
   `navController.navigate("history")`. Così `GameScreen` non sa nulla della
   navigazione — testabilità e separazione delle responsabilità.
3. **Lista partite: dove vive?** Se è in `MainActivity` con `rememberSaveable`,
   sopravvive alla rotazione. Se vuoi essere più "pulito", crea un `GameViewModel`
   con `viewModel()`. Per la consegna intermedia, `rememberSaveable` in
   MainActivity basta.

Quando avrai studiato bene **stato + state hoisting + navigation callback**,
questi pezzi si incastrano da soli. Ora il codice è "sbozzato bene" come
struttura visuale, ma il **flusso di dati e di eventi è ancora scollegato**: hai
i contenitori dello stato e le firme delle callback, ti manca solo collegare i
fili.
