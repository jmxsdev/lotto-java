# Ejercicios para practicar

**La respuesta corta:** ocho ejercicios en tres niveles. Cada uno tiene objetivo,
pista y criterio de aceptación. Las soluciones no se incluyen a propósito: el
valor está en que las construyas y las verifiques.

Antes de empezar, ten la aplicación corriendo (ver
[Ejecutar y depurar](08-ejecutar-y-depurar.md)) y recompila después de cada
cambio.

## Calentamiento

### 1. Cambiar un multiplicador

- **Objetivo:** que Lotto Activo pague 50 en lugar de 30.
- **Pista:** el multiplicador se pasa en el constructor de `Juego` dentro de
  `Catalogo.cargar()`. Busca el `new Juego(1, "Lotto Activo", ...)`. Recuerda que
  el constructor valida `premioMultiplo > 0`.
- **Criterio de aceptación:** el título del tablero muestra "premio x50"; al
  añadir 10,00 Bs la columna "Premio (Bs)" muestra 500,00.

### 2. Mostrar la cantidad de jugadas en otro sitio

- **Objetivo:** que la cantidad de jugadas también aparezca dentro de la ventana,
  no solo en el título.
- **Pista:** el título se actualiza en `VentanaTaquilla.refrescarTicket()` con
  `ticket.getCantidad()`. Añade un `JLabel` (por ejemplo en `PanelTicket`) y
  actualízalo desde `PanelTicket.actualizar(...)`, que ya recibe la lista.
- **Criterio de aceptación:** el número cambia al añadir, quitar, cobrar y
  limpiar; coincide siempre con el texto del título.

### 3. Explorar `BoxLayout`

- **Objetivo:** reorganizar la fila "Monto (Bs) + Añadir jugada" usando
  `BoxLayout` en lugar de `FlowLayout`.
- **Pista:** `BoxLayout` permite apilar en el eje X o en el eje Y
  (`BoxLayout.X_AXIS` / `BoxLayout.Y_AXIS`). Se aplica en `VentanaTaquilla` sobre
  `panelMonto`. Prueba ambos ejes y observa cómo cambia el comportamiento al
  redimensionar.
- **Criterio de aceptación:** los componentes se alinean sin deformarse y la
  ventana sigue siendo usable al agrandar y reducir.

## Intermedio

### 4. Agregar un juego nuevo al catálogo

- **Objetivo:** añadir un cuarto juego a `Catalogo.cargar()` con sus opciones,
  horarios y multiplicador.
- **Pista:** reutiliza el patrón de los juegos existentes: una `List<Opcion>` y
  una `List<String>`. Usa un `id` no repetido y un `tipo` nuevo. No hace falta
  tocar la GUI: el `JList` y el tablero se alimentan de `catalogo.getJuegos()`.
- **Criterio de aceptación:** el juego aparece en la lista; al seleccionarlo, el
  combo muestra sus horarios y el tablero sus opciones; se puede añadir una
  jugada y el premio se calcula con su multiplicador.

### 5. Mostrar el premio potencial de la opción elegida

- **Objetivo:** que, al elegir una opción, un `JLabel` muestre cuánto pagaría esa
  apuesta con el monto actual.
- **Pista:** `PanelTablero` expone `setAlSeleccionar(Consumer<Opcion>)` y avisa
  cada vez que cambia la selección, incluso con `null` cuando ya no hay ninguna.
  Regístralo desde `VentanaTaquilla` y usa `juego.premioPotencial(monto)`. Añade
  un `JLabel` al panel del monto y actualízalo también cuando cambie el
  `JSpinner` (necesitarás un `ChangeListener`).
- **Criterio de aceptación:** con monto 50 y "Tigre" en Lotto Activo, el label
  muestra `Bs. 1.500,00`; al limpiar la selección, el label vuelve a quedar
  vacío.

## Reto

### 6. Permitir seleccionar varios horarios

- **Objetivo:** que una misma apuesta se registre en más de un horario a la vez.
- **Pista:** el `JComboBox<String>` actual es de selección única. Sustitúyelo por
  un `JList<String>` con `setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)`
  y, en `anadirJugada()`, recorre los horarios seleccionados creando una `Jugada`
  por cada uno. El `JList` necesita un `DefaultListModel` que rellenes en
  `alCambiarJuego()`.
- **Criterio de aceptación:** con dos horarios seleccionados, una opción y un
  monto, "Añadir jugada" crea dos filas; el total suma ambos montos; si no hay
  ningún horario seleccionado se muestra un aviso.

### 7. Guardar los tickets en un archivo

- **Objetivo:** persistir cada ticket cobrado en disco para no perderlo al cerrar.
- **Pista:** `VentanaTaquilla.construirTextoTicket()` ya genera el texto.
  Añade un método que lo escriba al final de un archivo (por ejemplo
  `tickets.txt`). Usa `Files.writeString` con `StandardOpenOption.APPEND`, o
  `BufferedWriter`. Maneja `IOException` mostrando un aviso con `mostrarAviso`.
  Decide si quieres además poder releerlos.
- **Criterio de aceptación:** al cobrar, el texto queda anexado al archivo;
  cobrar dos veces añade dos bloques; si el archivo no se puede escribir, la app
  avisa sin cerrarse.

### 8. Agregar emojis con fuente embebida

- **Objetivo:** mostrar un emoji por opción aunque el sistema no tenga fuentes de
  emoji instaladas.
- **Pista:** el campo `icono` de `Opcion` existe pero en el catálogo siempre es
  `null`. Puedes usarlo para guardar un emoji por animalito/signo. Para
  garantizar que se vea, embebe un archivo `.ttf` con símbolos y cárgalo con
  `Font.createFont(Font.TRUETYPE_FONT, archivo)`, registrándolo con
  `GraphicsEnvironment.registerFont`. Luego aplica esa fuente a los botones del
  tablero.
- **Criterio de aceptación:** en una máquina Linux sin fuentes de emoji del
  sistema, cada botón muestra su símbolo dentro del texto; el diseño no se
  rompe y la app arranca aunque falte el archivo de fuente (con aviso, no con
  excepción no controlada).

## Siguiente

Cuando tengas dudas sobre algún término al resolver los ejercicios, consulta el
[Glosario](10-glosario.md).
