# Recorrido de la GUI

**La respuesta corta:** seis clases viven en `GUI/`. `Formato` da formato a la
moneda, `Botonera` es una grilla de botones reutilizable, `PanelTablero` muestra
las opciones, `JugadaTableModel` adapta el `Ticket` a la tabla, `PanelTicket`
muestra el ticket y `VentanaTaquilla` lo ensambla y coordina todo.

Se recorren en orden de dependencia: de las utilidades a la ventana.

## `Formato` — moneda en bolívares

**Rol:** convertir un `double` en texto de moneda venezolana. Es una clase de
utilidad: constructor privado y métodos estáticos.

```java
private static final DecimalFormat BS =
        new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.of("es", "VE")));

public static String numero(double v) {
    synchronized (BS) {
        return BS.format(v);
    }
}

public static String bs(double v) {
    return "Bs. " + numero(v);
}
```

**¿Por qué `Locale.of("es","VE")`?** El separador decimal y el de miles no son
universales. Con la configuración venezolana, el resultado real es:

| Entrada | `Formato.bs(...)` |
|---------|-------------------|
| `10` | `Bs. 10,00` |
| `1234.5` | `Bs. 1.234,50` |

Es decir: **coma** como separador decimal y **punto** como separador de miles.

**¿Por qué se sincroniza sobre `BS`?** `DecimalFormat` **no es thread-safe**. Un
`DecimalFormat` compartido entre hilos puede producir resultados inconsistentes o
lanzar excepciones internas si dos hilos formatean a la vez. El bloque
`synchronized (BS)` garantiza acceso exclusivo. Es el patrón habitual de Java
para compartir un formateador estático.

> Nota de estilo moderno: la alternativa contemporánea sería crear un
> formateador nuevo por llamada, o usar `ThreadLocal`. El proyecto eligió el
> bloqueo explícito porque es simple y suficientemente rápido para esta escala.

## `Botonera` — grilla de botones reutilizable

**Rol:** mostrar una fila de botones en rejilla y resaltar cuál está seleccionado.
**No conoce el dominio**: solo recibe captions y avisa. Por eso puede usarse para
animalitos, terminales o signos sin cambios.

**Construcción:**

```java
setLayout(new GridLayout(0, columnas, 4, 4));
botones = new JButton[captions.length];
for (int i = 0; i < captions.length; i++) {
    botones[i] = new JButton(captions[i]);
    add(botones[i]);
}

bordeNormal = botones.length > 0 ? botones[0].getBorder() : null;
bordeSeleccion = BorderFactory.createLineBorder(new Color(0x1565C0), 3);
```

`GridLayout(0, columnas, …)` significa "filas necesarias, tantas columnas como
pida el llamador". Los bordes se guardan una vez: el normal se toma del primer
botón (antes de tocarlo) y el de selección es un borde azul de 3 píxeles.

**Cómo se intercambian los bordes en `marcarSeleccion`:**

```java
public void marcarSeleccion(int indice) {
    if (indiceSeleccionado >= 0 && indiceSeleccionado < botones.length) {
        botones[indiceSeleccionado].setBorder(bordeNormal);
    }
    if (indice >= 0 && indice < botones.length) {
        botones[indice].setBorder(bordeSeleccion);
        indiceSeleccionado = indice;
    }
}
```

El algoritmo es de dos pasos: **primero borra** el resaltado del índice anterior
(si lo hay) y **después pinta** el nuevo. Olvidar el primer paso dejaría varios
botones marcados a la vez. `limpiarSeleccion()` es la versión que solo borra.

`getBoton(int)` valida el rango y lanza `IndexOutOfBoundsException` si no existe.

## `PanelTablero` — el tablero de opciones

**Rol:** mostrar las opciones del juego activo y exponer cuál fue elegida.

**Método central, `mostrar(Juego)`:**

```java
public void mostrar(Juego juego) {
    this.juego = juego;
    this.seleccionada = null;

    titulo.setText(juego.getNombre()
            + "   (premio x" + (int) juego.getPremioMultiplo() + ")");

    contenedor.removeAll();

    List<Opcion> opciones = juego.getOpciones();
    String[] captions = new String[opciones.size()];
    for (int i = 0; i < opciones.size(); i++) {
        captions[i] = opciones.get(i).getEtiquetaTablero();
    }

    int columnas = opciones.size() > 40 ? 10 : opciones.size() > 12 ? 5 : 4;
    botonera = new Botonera(captions, columnas);
    for (int i = 0; i < captions.length; i++) {
        final int indice = i;
        botonera.getBoton(i).addActionListener(e -> seleccionar(indice));
    }

    contenedor.add(botonera, BorderLayout.CENTER);
    contenedor.revalidate();
    contenedor.repaint();
}
```

Puntos clave:

- Usa `getEtiquetaTablero()` como caption; por eso el tablero muestra `Aries`,
  `00` o `10 · Tigre` según el caso.
- El número de columnas depende de cuántas opciones tenga el juego.
- Reconstruye la `Botonera` cada vez y luego llama `revalidate()` + `repaint()`:
  tras añadir o quitar componentes hay que pedirle a Swing que recalcule el
  layout y vuelva a pintar.

**Cómo usa `Consumer<Opcion>`:** el panel no decide qué pasa cuando se elige una
opción; solo lo notifica.

```java
private Consumer<Opcion> alSeleccionar;

public void setAlSeleccionar(Consumer<Opcion> cb) {
    this.alSeleccionar = cb;
}

private void seleccionar(int indice) {
    seleccionada = juego.getOpciones().get(indice);
    botonera.marcarSeleccion(indice);
    notificarSeleccion();
}

private void notificarSeleccion() {
    if (alSeleccionar != null) {
        alSeleccionar.accept(seleccionada);
    }
}
```

La comprobación `alSeleccionar != null` evita un `NullPointerException` si nadie
registró el callback. `VentanaTaquilla` sí lo registra para habilitar "Añadir
jugada" solo cuando hay selección:

```java
panelTablero.setAlSeleccionar(opcion -> btnAnadir.setEnabled(opcion != null));
```

Por eso `limpiarSeleccion()` y `mostrar(...)` también llaman a
`notificarSeleccion()`: así el observador recibe `null` y el botón vuelve a
deshabilitarse cuando corresponde.

## `JugadaTableModel` — adaptador `Ticket` → `JTable`

**Rol:** darle a `JTable` los datos de las jugadas sin que la tabla guarde estado
propio.

Extiende `AbstractTableModel` e implementa lo mínimo: cuenta de filas, cuenta de
columnas y valor por celda.

```java
private static final String[] COLUMNAS = {
    "Juego", "Horario", "Jugada", "Monto (Bs)", "Premio (Bs)"
};
```

**Columna por columna (`getValueAt`):**

| # | Columna | Devuelve | De dónde sale |
|---|---------|----------|---------------|
| 0 | Juego | Nombre del juego | `jugada.getJuego().getNombre()` |
| 1 | Horario | Horario de la apuesta | `jugada.getHorario()` |
| 2 | Jugada | Etiqueta de la opción | `jugada.getOpcion().getEtiquetaTablero()` |
| 3 | Monto (Bs) | Monto formateado | `Formato.numero(jugada.getMonto())` |
| 4 | Premio (Bs) | Premio potencial formateado | `Formato.numero(jugada.getPremioPotencial())` |

**Refresco y copia defensiva:**

```java
public void setJugadas(List<Jugada> nuevas) {
    this.jugadas = (nuevas == null) ? new ArrayList<>() : new ArrayList<>(nuevas);
    fireTableDataChanged();
}
```

`fireTableDataChanged()` es el aviso que hace que la `JTable` se vuelva a pintar.
Sin él, la tabla seguiría mostrando datos viejos aunque la lista interna cambie.

`isCellEditable` devuelve siempre `false`: las celdas se ven pero no se editan.
`getJugadaEn(int)` permite al controlador recuperar la `Jugada` de una fila.

## `PanelTicket` — la vista del ticket

**Rol:** mostrar la tabla de jugadas, el total, y exponer las acciones "Quitar" y
"Cobrar". No contiene lógica de negocio.

**Las dos acciones se exponen como `Runnable`:**

```java
public void setAlQuitar(Runnable r) {
    this.alQuitar = r;
    btnQuitar.addActionListener(e -> {
        if (alQuitar != null) {
            alQuitar.run();
        }
    });
}
```

`setAlCobrar` es idéntico para `btnCobrar`. La ventana conecta los métodos reales:

```java
panelTicket.setAlQuitar(this::quitarJugada);
panelTicket.setAlCobrar(this::cobrarTicket);
```

**`actualizar` refresca la vista y habilita/deshabilita botones:**

```java
public void actualizar(List<Jugada> jugadas, double total) {
    modelo.setJugadas(jugadas);
    lblTotal.setText("Total: " + Formato.bs(total));
    boolean vacio = jugadas == null || jugadas.isEmpty();
    btnCobrar.setEnabled(!vacio);
    btnQuitar.setEnabled(!vacio);
}
```

Con el ticket vacío, ambos botones quedan deshabilitados: es la propia vista la
que refleja el estado, sin decidir reglas de negocio.

## `VentanaTaquilla` — la ventana y el controlador

`VentanaTaquilla` es un `JFrame` que además coordina el Modelo y las vistas.
Método por método:

### `construirInterfaz()`

Crea el `Catalogo` y el `Ticket` y monta toda la pantalla:

- **Encabezado (`panelNorte`):** título a la izquierda y "Limpiar ticket" a la derecha.
- **Columna izquierda (`panelOeste`):** `JList<Juego>` de juegos y `JComboBox<String>` de horarios.
- **Centro:** `panelTablero`, envuelto en un `JScrollPane`.
- **Columna derecha (`panelEste`):** fila de monto (`JSpinner` + "Añadir jugada") y `panelTicket`.

El `JList` se llena recorriendo `catalogo.getJuegos()`, y se preselecciona el
primero:

```java
listaJuegos = new JList<>(modeloLista);
listaJuegos.setSelectedIndex(0);
```

El monto usa `SpinnerNumberModel(10.0, 0.0, 100000.0, 1.0)`: valor inicial 10,
mínimo 0, máximo 100000, paso 1. El botón "Añadir jugada" nace deshabilitado
(`btnAnadir.setEnabled(false)`) y solo se habilita cuando hay una opción elegida.

### `conectarEventos()`

Registra los listeners y sincroniza el estado inicial:

```java
listaJuegos.addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        alCambiarJuego();
    }
});
btnAnadir.addActionListener(e -> anadirJugada());
btnLimpiar.addActionListener(e -> limpiarTicket());
panelTicket.setAlQuitar(this::quitarJugada);
panelTicket.setAlCobrar(this::cobrarTicket);
panelTablero.setAlSeleccionar(opcion -> btnAnadir.setEnabled(opcion != null));

alCambiarJuego();
refrescarTicket();
```

El chequeo `!e.getValueIsAdjusting()` evita disparar el cambio de juego a
mediados de la selección; solo actúa cuando el usuario terminó de elegir. La
línea de `setAlSeleccionar` conecta el tablero con el botón de añadir. Las dos
últimas llamadas muestran el juego preseleccionado y pintan el ticket vacío al
abrir.

### `alCambiarJuego()`

Rellena el combo de horarios y pide al tablero que se redibuje:

```java
Juego juego = listaJuegos.getSelectedValue();
if (juego == null) {
    return;
}
comboHorarios.removeAllItems();
for (String horario : juego.getHorarios()) {
    comboHorarios.addItem(horario);
}
panelTablero.mostrar(juego);
```

### `anadirJugada()`

Valida en orden y, si todo está bien, construye la jugada:

```java
Opcion opcion = panelTablero.getSeleccionada();
if (opcion == null) {
    mostrarAviso("Seleccione una opción en el tablero.");
    return;
}
// ... valida horario y monto ...

ticket.agregar(new Jugada(juego, horario, opcion, monto));
refrescarTicket();
panelTablero.limpiarSeleccion();
```

Validaciones: juego seleccionado, opción seleccionada, horario seleccionado y
monto mayor que cero. Cada fallo muestra un aviso y **corta con `return`**. Tras
agregar, refresca la tabla y limpia la selección del tablero.

### `quitarJugada()`

```java
int fila = panelTicket.getFilaSeleccionada();
if (fila < 0) {
    mostrarAviso("Seleccione una jugada de la tabla.");
    return;
}
ticket.quitar(fila);
refrescarTicket();
```

La fila seleccionada de la `JTable` se corresponde con el índice en el `Ticket`,
así que el índice viaja directo.

### `cobrarTicket()`

```java
if (ticket.getCantidad() == 0) {
    mostrarAviso("No hay jugadas en el ticket.");
    return;
}

JTextArea area = new JTextArea(construirTextoTicket());
area.setEditable(false);
area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

JScrollPane scroll = new JScrollPane(area);
scroll.setPreferredSize(new Dimension(480, 320));

JOptionPane.showMessageDialog(this, scroll, "Ticket", JOptionPane.INFORMATION_MESSAGE);

ticket.limpiar();
refrescarTicket();
```

Usa fuente monoespaciada para que el ticket se vea alineado. Tras el diálogo,
limpia el ticket y refresca. `construirTextoTicket()` arma el texto con un
`StringBuilder`, separadores de `=` y `-`, y los datos de cada jugada (juego,
horario, etiqueta, monto, premio y multiplicador), cerrando con total y cantidad.

> Detalle: `cobrarTicket` no limpia la selección del tablero. Solo "Limpiar
> ticket" lo hace.

### `refrescarTicket()`

```java
panelTicket.actualizar(ticket.getJugadas(), ticket.getTotal());
setTitle("Taquilla de Loterías — " + ticket.getCantidad() + " jugada(s)");
```

Es el único punto que sincroniza la vista del ticket con el Modelo. Centralizarlo
evita olvidos: cualquier cambio del ticket termina llamando aquí.

### `mostrarAviso(String)` y `main`

`mostrarAviso` usa `JOptionPane.showMessageDialog` con `WARNING_MESSAGE`. `main`
arranca en el EDT:

```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new VentanaTaquilla().setVisible(true));
}
```

## Siguiente

Con todas las piezas vistas, el siguiente documento las pone en movimiento:
[Flujo de una venta](07-flujo-de-una-venta.md).
