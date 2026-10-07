# Recorrido del Modelo

**La respuesta corta:** cinco clases representan todo el negocio. `Opcion` es un
valor apostable; `Juego` agrupa opciones, horarios y multiplicador; `Jugada` es
una apuesta registrada; `Ticket` acumula jugadas; `Catalogo` fabrica los juegos
disponibles. Ninguna importa Swing.

Este recorrido sigue el orden lógico del dominio: de la pieza más pequeña al
contenedor de todo.

## `Opcion` — un valor apostable

**Propósito:** representar cualquier cosa sobre la que se puede apostar, sin
importar el juego: un animalito con número, un número de terminal o un signo
zodiacal.

**Campos:**

| Campo | Tipo | Notas |
|-------|------|-------|
| `numero` | `Integer` | Puede ser `null` (los signos no tienen número) |
| `label` | `String` | Texto principal; obligatorio |
| `value` | `String` | Identificador interno; obligatorio |
| `icono` | `String` | Reservado para un ícono; en el catálogo siempre es `null` |

**Validaciones del constructor** (`IllegalArgumentException`): `label` y `value`
no pueden ser nulos ni estar en blanco. `numero` e `icono` sí admiten `null`.

**La pieza clave: `getEtiquetaTablero()`.** Decide cómo se muestra la opción en
el tablero y resuelve exactamente tres casos:

```java
public String getEtiquetaTablero() {
    if (numero == null) {
        return label;
    }
    if (value.equals(String.valueOf(numero))) {
        return label;
    }
    return numero + " · " + label;
}
```

| Caso | Condición | Ejemplo real | Salida |
|------|-----------|--------------|--------|
| Sin número | `numero == null` | Signo Aries (número `null`) | `Aries` |
| Terminal | `value` coincide con el número formateado | Terminal `0` (`label = "00"`, `value = "0"`) | `00` |
| Animalito | Ninguna de las anteriores | `numero = 10`, `label = "Tigre"` | `10 · Tigre` |

Detalle fino: el caso terminal compara `value.equals(String.valueOf(numero))`.
Para el `0` de terminal, `value` es `"0"` y `String.valueOf(0)` también es
`"0"`, así que devuelve el `label` (`"00"`). Así se evita mostrar `0 · 00`.

Contraste útil: Ballena también tiene `numero = 0`, pero su `value` es
`"ballena"`. Como `"ballena"` no es igual a `"0"`, cae en el tercer caso y se
muestra como `0 · Ballena`, junto a `0 · Delfín`.

**`toString()`** devuelve `label`. Por eso un `JList<Opcion>` mostraría el nombre
legible sin conversiones manuales.

## `Juego` — un juego vendible

**Propósito:** definir un juego: su catálogo de opciones, sus horarios y cuánto
paga.

**Campos:** `id`, `nombre`, `tipo`, `premioMultiplo`, `opciones` (`List<Opcion>`),
`horarios` (`List<String>`).

**Validaciones:** `nombre` y `tipo` no vacíos; `premioMultiplo > 0`.

**Copias defensivas en el constructor:**

```java
this.opciones = (opciones == null) ? new ArrayList<>() : new ArrayList<>(opciones);
this.horarios = (horarios == null) ? new ArrayList<>() : new ArrayList<>(horarios);
```

Si la lista entrante es `null`, se crea una vacía; si no, se copia. Así, cambiar
la lista original después de construir el `Juego` no lo afecta.

**Getters protegidos:** `getOpciones()` y `getHorarios()` devuelven
`Collections.unmodifiableList(...)`. Quien intente añadir o quitar recibe una
excepción en tiempo de ejecución.

**Cálculo del premio:**

```java
public double premioPotencial(double monto) {
    return monto * premioMultiplo;
}
```

Con Lotto Activo (`premioMultiplo = 30`), 10,00 Bs dan un premio potencial de
300,00 Bs.

**`getCantidadOpciones()`** devuelve `opciones.size()`. **`toString()`** devuelve
`nombre`, de modo que la lista de juegos muestra el nombre directamente.

## `Jugada` — una línea del ticket

**Propósito:** una apuesta ya registrada: juego + horario + opción + monto.

**Campos:** `juego` (`Juego`), `horario` (`String`), `opcion` (`Opcion`),
`monto` (`double`). Todos `private final`: es inmutable.

**Validaciones:** `juego` y `opcion` no nulos; `horario` no vacío; `monto > 0`.

**Delegación del premio:**

```java
public double getPremioPotencial() {
    return juego.premioPotencial(monto);
}
```

La `Jugada` no repite la fórmula: le pide al `Juego` que la calcule. Esto es
**delegación**: la regla de pago vive en un solo lugar, el `Juego`.

Nota: `Jugada` no sobrescribe `toString()`. Su representación en la tabla la
decide `JugadaTableModel`, no la clase.

## `Ticket` — el ticket en curso

**Propósito:** acumular las jugadas mientras el taquillero trabaja.

**Campo único:** `private final List<Jugada> jugadas = new ArrayList<>()`.

A diferencia de las clases anteriores, `Ticket` **es mutable** por diseño: va
creciendo y decreciendo. Lo que no cambia es la referencia a la lista.

**Operaciones:**

| Método | Qué hace | Falla con |
|--------|----------|-----------|
| `agregar(Jugada)` | Añade una jugada | `IllegalArgumentException` si es `null` |
| `quitar(int)` | Elimina por índice | `IndexOutOfBoundsException` si el índice está fuera de rango |
| `limpiar()` | Vacía el ticket | — |
| `getJugadas()` | Lista no modificable | — |
| `getTotal()` | Suma de montos | — |
| `getCantidad()` | Número de jugadas | — |

**`quitar` valida antes de remover**, con un mensaje que incluye el índice:

```java
public void quitar(int indice) {
    if (indice < 0 || indice >= jugadas.size()) {
        throw new IndexOutOfBoundsException("Índice de jugada fuera de rango: " + indice);
    }
    jugadas.remove(indice);
}
```

**`getTotal` recorre y suma:**

```java
public double getTotal() {
    double total = 0;
    for (Jugada jugada : jugadas) {
        total += jugada.getMonto();
    }
    return total;
}
```

Nótese que el total suma **montos**, no premios potenciales: lo que el cliente
paga es lo que se muestra como total.

## `Catalogo` — la fuente de juegos

**Propósito:** entregar los juegos disponibles. En producción vendrían de un
archivo JSON o de una API; aquí se cargan en memoria.

**Campo:** `private final List<Juego> juegos = new ArrayList<>()`.

El constructor solo llama a `cargar()`:

```java
public Catalogo() {
    cargar();
}
```

**Cómo se construye cada juego:**

- **Lotto Activo** (`id = 1`, tipo `animalitos`, multiplicador `30`): 38 opciones
  declaradas con `List.of(...)`. Ballena y Delfín comparten el número `0`; los
  números continúan hasta `36` (Culebra). 12 horarios de 08:00 a 19:00.
- **Terminal Activo** (`id = 3`, tipo `terminales`, multiplicador `60`): las 100
  opciones se generan con un bucle, no a mano.

```java
for (int n = 0; n <= 99; n++) {
    terminales.add(new Opcion(n, String.format("%02d", n), String.valueOf(n), null));
}
```

  Aquí se ve el caso terminal de `getEtiquetaTablero()`: `label` es `"00"`,
  `"01"`, …, `"99"` (formateado con `%02d`) y `value` es `"0"`, `"1"`, … Comparte
  la lista de horarios con Lotto Activo. Mismo `id` consecutivo: `3`.
- **Triple Zulia** (`id = 2`, tipo `tripletas`, multiplicador `600`): 12 signos
  con `numero = null` y `value` de tres letras (`"ARI"`, `"TAU"`, …). 3 horarios:
  `12:45`, `16:45`, `19:05`.

**`getJuegos()`** devuelve `Collections.unmodifiableList(juegos)`.

Dato para retener: aunque Triple Zulia tiene `id = 2`, se añade **después** de
Terminal Activo (`id = 3`). El orden de la lista es Lotto, Terminal, Triple, y
ese es el orden que ve la interfaz.

## Siguiente

Con el Modelo completo, pasamos al otro lado del límite:
[Recorrido de la GUI](06-gui-linea-a-linea.md).
