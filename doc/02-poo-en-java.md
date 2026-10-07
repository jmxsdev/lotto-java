# POO aplicada al proyecto

**La respuesta corta:** el paquete `Modelo` son objetos que representan el
negocio de la taquilla. Cada clase es un concepto del mundo real —un juego, una
opción, una jugada, un ticket— y cada objeto es una instancia concreta de ese
concepto. La GUI observa y manipula esos objetos, pero nunca los reemplaza.

Este documento recorre los conceptos de Programación Orientada a Objetos usando
**solo ejemplos de este proyecto**.

## Clase vs. objeto

Una **clase** es el molde; un **objeto** es una instancia concreta.

`Opcion` es la clase que describe cualquier valor apostable:

```java
public final class Opcion {
    private final Integer numero;
    private final String label;
    private final String value;
    private final String icono;
    // ...
}
```

Cada animalito del catálogo es un **objeto** construido con ese molde:

```java
new Opcion(0, "Ballena", "ballena", null),
new Opcion(1, "Carnero", "carnero", null),
```

El molde es uno; las instancias son 38 para Lotto Activo, 100 para Terminal
Activo y 12 para Triple Zulia.

## Estado vs. comportamiento

- **Estado** = los campos (los datos que el objeto guarda).
- **Comportamiento** = los métodos (lo que el objeto sabe hacer).

En `Opcion`, el estado son `numero`, `label`, `value` e `icono`. El
comportamiento es, por ejemplo, `getEtiquetaTablero()`:

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

El objeto no "pregunta" a la interfaz cómo mostrarse: **él mismo sabe
construir su etiqueta**. Eso es comportamiento del dominio.

## Encapsulamiento: `private final` y getters

Todos los campos de `Juego` son privados y finales, y el acceso se hace por
getters:

```java
private final int id;
private final String nombre;
private final String tipo;
private final double premioMultiplo;
private final List<Opcion> opciones;
private final List<String> horarios;
```

¿Por qué? Porque el objeto controla sus propias reglas. Nadie de fuera puede
cambiar el multiplicador de un juego por accidente: si no hay setter, no hay
forma de dejarlo en un estado inválido.

No es burocracia: es una **garantía**. Cuando ves `private final` en este
proyecto, significa "esto se asigna una vez y ya no se toca".

## Inmutabilidad y copias defensivas

`Opcion` y `Jugada` son **inmutables**: la clase es `final`, los campos son
`final` y no existen setters. Una vez creadas, sus valores no cambian.

```java
public final class Jugada {
    private final Juego juego;
    private final String horario;
    private final Opcion opcion;
    private final double monto;
    // ...
}
```

Inmutabilidad total no siempre es posible cuando se guardan colecciones. Ahí
entran las **copias defensivas**, que tienen dos lados:

**Al recibir** una lista, se copia para que nadie la modifique desde fuera:

```java
// Juego.java (constructor)
this.opciones = (opciones == null) ? new ArrayList<>() : new ArrayList<>(opciones);
```

**Al entregar** una lista, se devuelve una vista no modificable:

```java
// Juego.java
public List<Opcion> getOpciones() {
    return java.util.Collections.unmodifiableList(opciones);
}
```

La misma defensa aparece en `Ticket.getJugadas()`, en `Catalogo.getJuegos()` y en
`JugadaTableModel.setJugadas()` (que también copia con `new ArrayList<>(nuevas)`).
La idea es constante: **nadie de fuera puede alterar el estado interno del
objeto pasando por encima de sus reglas**.

## Composición: "tiene un"

La composición es cuando un objeto contiene a otro. Se lee como "tiene un".

- Un `Ticket` **tiene** muchas `Jugada`: `private final List<Jugada> jugadas`.
- Una `Jugada` **tiene** un `Juego`, un `Opcion`, un horario y un monto.
- Un `Juego` **tiene** una lista de `Opcion` y una lista de horarios.

Y el ensamblaje se ve en el ticket: el controlador construye cada jugada a
partir de las piezas que el taquillero eligió:

```java
ticket.agregar(new Jugada(juego, horario, opcion, monto));
```

Ninguna de estas clases "es un" tipo de otra: **se componen**.

## Herencia vs. composición: por qué aquí se prefirió composición

La herencia ("es un") acopla fuerte: el hijo queda atado al comportamiento del
padre. En este dominio no hay jerarquías naturales entre `Juego`, `Jugada` y
`Ticket`; son conceptos distintos que colaboran. Por eso se **componen**: un
`Ticket` usa `Jugada`, no "es una" `Jugada`.

La única herencia real está del lado de la vista, y es una herencia impuesta por
el framework:

```java
public class JugadaTableModel extends AbstractTableModel { ... }
```

`JugadaTableModel` "es un" `AbstractTableModel` porque `JTable` exige esa clase
base para dibujar filas y columnas. Ahí la herencia es la herramienta correcta.
En el dominio, no.

## Abstracción: clases abstractas e interfaces

La **abstracción** separa *qué* se necesita de *cómo* se implementa.

- `AbstractTableModel` es una **clase abstracta**: define el contrato de un
  modelo de tabla y obliga a implementar `getRowCount`, `getColumnCount` y
  `getValueAt`. `JugadaTableModel` completa lo que falta.
- `ActionListener` es una **interfaz** funcional: un solo método, `actionPerformed`.
  El proyecto la usa a través de lambdas.
- `Consumer<Opcion>` es una **interfaz funcional** del JDK para "recibe una
  opción y haz algo".
- `Runnable` es otra interfaz funcional: "ejecuta una acción sin argumentos".

> Para la diferencia entre **interfaz** y **clase abstracta**, y por qué ambas son
> *contratos*, ver
> [Contratos: interfaces y clases abstractas](12-contratos-e-interfaces.md).

`PanelTablero` expone un hueco de comportamiento que no implementa:

```java
private Consumer<Opcion> alSeleccionar;

public void setAlSeleccionar(Consumer<Opcion> cb) {
    this.alSeleccionar = cb;
}
```

`PanelTicket` hace lo mismo con dos acciones sin argumentos:

```java
private Runnable alQuitar;
private Runnable alCobrar;
```

El panel no decide qué pasa al cobrar; solo ofrece el punto de conexión.

## Polimorfismo: sobrescritura de `toString` y `getValueAt`

El polimorfismo es que un mismo mensaje se resuelva según el objeto que lo
recibe. Aquí se ve en dos lugares concretos.

**`toString` sobrescrito.** `Opcion` y `Juego` lo redefinen para mostrar algo
legible:

```java
// Opcion.java
@Override
public String toString() {
    return label;
}

// Juego.java
@Override
public String toString() {
    return nombre;
}
```

Gracias a eso, un `JList<Juego>` muestra los nombres de los juegos sin que el
controlador tenga que convertir nada a mano.

**`getValueAt` sobrescrito.** `JugadaTableModel` decide qué texto aparece en cada
celda:

```java
@Override
public Object getValueAt(int fila, int columna) {
    Jugada jugada = jugadas.get(fila);
    switch (columna) {
        case 0: return jugada.getJuego().getNombre();
        case 1: return jugada.getHorario();
        case 2: return jugada.getOpcion().getEtiquetaTablero();
        case 3: return Formato.numero(jugada.getMonto());
        case 4: return Formato.numero(jugada.getPremioPotencial());
        default: return "";
    }
}
```

`JTable` llama a este método sin saber de lotería; cada celda se resuelve por
polimorfismo.

## Validación en constructores con `IllegalArgumentException`

Los objetos del Modelo se niegan a nacer inválidos. Cada constructor valida sus
argumentos y lanza `IllegalArgumentException` si no cumplen las reglas.

| Clase | Qué rechaza |
|-------|-------------|
| `Opcion` | `label` o `value` nulos o vacíos |
| `Juego` | `nombre` o `tipo` vacíos; `premioMultiplo <= 0` |
| `Jugada` | `juego` u `opcion` nulos; `horario` vacío; `monto <= 0` |
| `Ticket.agregar` | una jugada nula |
| `Ticket.quitar` | un índice fuera de rango (`IndexOutOfBoundsException`) |
| `Botonera` | `columnas <= 0` |

Ejemplo real de `Jugada`:

```java
if (monto <= 0) {
    throw new IllegalArgumentException("El monto de la jugada debe ser mayor que cero.");
}
```

Esto se llama **fallar temprano**: es mejor detener la construcción con un
mensaje claro que arrastrar un objeto inconsistente por toda la aplicación.

## Resumen

| Clase | Qué representa | Qué garantiza |
|-------|----------------|---------------|
| `Opcion` | Un valor apostable (animalito, terminal o signo) | Etiqueta y value no vacíos; inmutable |
| `Juego` | Un juego vendible con sus opciones y horarios | Nombre/tipo válidos; multiplicador > 0; listas copiadas |
| `Jugada` | Una línea del ticket | Juego y opción no nulos; monto > 0; inmutable |
| `Ticket` | El ticket en curso | Solo acepta jugadas válidas; lista no modificable desde fuera |
| `Catalogo` | Los juegos disponibles | Se carga una sola vez al construir; lista no modificable |
| `JugadaTableModel` | Adaptador `Ticket` → `JTable` | Copia su lista; no es editable |

## Siguiente

Con los objetos claros, el siguiente paso es ver cómo se reparten las
responsabilidades: [Arquitectura MVC](03-arquitectura-mvc.md).
