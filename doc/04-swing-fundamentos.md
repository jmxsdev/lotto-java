# Apps de escritorio con Swing

**La respuesta corta:** Swing es el toolkit de interfaz gráfica de Java. Con él
se construyen ventanas, botones, tablas y listas que corren en el escritorio. En
este proyecto Swing vive **solo** en el paquete `GUI`; el Modelo no lo toca.

## Qué es Swing y de dónde viene

Swing forma parte de las **Java Foundation Classes (JFC)** y viene incluido en el
JDK desde 1998 (Java 2). Se construyó sobre AWT, pero con un giro importante:
sus componentes son *ligeros* (dibujados por Java, no por el sistema operativo),
por lo que se ven igual en Windows, macOS y Linux.

- **AWT** aporta lo básico: ventanas reales del sistema, eventos, colores.
- **Swing** aporta los componentes ricos y *look and feel* configurable.

En Java moderno, Swing sigue siendo la opción estándar para utilidades de
escritorio sin dependencias externas. Este proyecto no usa JavaFX ni ninguna
librería adicional.

## Contenedores: de la ventana al panel

La jerarquía de contención es la columna vertebral de cualquier app Swing:

```
JFrame                     ← la ventana del sistema operativo
 └── contentPane (JPanel)  ← el área interior donde se colocan los componentes
      ├── panelNorte  (JPanel)
      ├── panelOeste  (JPanel)
      ├── panelTablero (JPanel)
      └── panelEste   (JPanel)
```

En `VentanaTaquilla.construirInterfaz()` se ve el patrón explícito:

```java
setLayout(new BorderLayout(8, 8));
((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
...
add(panelNorte, BorderLayout.NORTH);
add(panelOeste, BorderLayout.WEST);
add(new JScrollPane(panelTablero), BorderLayout.CENTER);
add(panelEste, BorderLayout.EAST);
```

Los `JPanel` intermedios agrupan componentes relacionados. Es una práctica
constante en el proyecto: **cada zona de la pantalla es un `JPanel` con su propio
layout**, y esos paneles se anidan.

## Componentes usados

| Componente | Para qué sirve | Dónde aparece en el proyecto |
|------------|----------------|------------------------------|
| `JFrame` | Ventana principal | `VentanaTaquilla` |
| `JPanel` | Contenedor y agrupador | Casi todo: `PanelTablero`, `PanelTicket`, `Botonera`, paneles de `VentanaTaquilla` |
| `JLabel` | Textos e instrucciones | Título, "Horario:", "Total: …" |
| `JButton` | Acciones | Botones del tablero, "Añadir jugada", "Limpiar ticket", "Cobrar ticket" |
| `JSpinner` | Valor numérico ajustable | Monto en Bs |
| `JComboBox` | Selección desplegable | Horarios |
| `JList` | Selección de una lista | Juegos |
| `JTable` | Datos tabulares | Jugadas del ticket |
| `JScrollPane` | Barras de desplazamiento | Envuelve la tabla, la lista de juegos y el tablero |
| `JTextArea` | Texto multilínea | Resumen del ticket al cobrar |
| `JOptionPane` | Diálogos modales | Avisos y resumen del ticket |

## Layouts y cuándo usar cada uno

Un **layout manager** decide dónde y cómo se colocan los hijos de un contenedor.
Elegir mal el layout es la causa más común de interfaces deformes.

| Layout | Cuándo usarlo | Dónde se usa aquí |
|--------|---------------|-------------------|
| `BorderLayout` | Zonas NORTE/SUR/ESTE/OESTE/CENTRO | `VentanaTaquilla`, `PanelTablero`, `PanelTicket`, paneles internos |
| `GridLayout` | Rejilla homogénea de celdas iguales | `Botonera` (el tablero de opciones) |
| `FlowLayout` | Fila de componentes que fluye | Fila "Monto + Añadir"; botones del ticket |

El layout del tablero se decide dinámicamente según cuántas opciones tenga el
juego (`PanelTablero.mostrar`):

```java
int columnas = opciones.size() > 40 ? 10 : opciones.size() > 12 ? 5 : 4;
botonera = new Botonera(captions, columnas);
```

Resultado real: Terminal Activo (100 opciones) → 10 columnas; Lotto Activo
(38) → 5 columnas; Triple Zulia (12) → 4 columnas.

## El modelo de eventos: listener + lambda

Swing no "consulta" botones: los botones **avisan**. El patrón es registrar un
*listener* que se ejecuta cuando ocurre el evento.

Forma clásica (una clase anónima que implementa `ActionListener`):

```java
boton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) { /* ... */ }
});
```

Forma moderna del proyecto (lambda), porque `ActionListener` es una interfaz
funcional:

```java
// PanelTablero.mostrar()
botonera.getBoton(i).addActionListener(e -> seleccionar(indice));
```

El `i` se copia a una variable final para poder capturarlo dentro de la lambda
(las lambdas capturan valores, no variables mutables):

```java
for (int i = 0; i < captions.length; i++) {
    final int indice = i;
    botonera.getBoton(i).addActionListener(e -> seleccionar(indice));
}
```

Otros tipos de evento usados en el proyecto:

| Evento | Listener | Dónde |
|--------|----------|-------|
| Clic en botón | `ActionListener` | Botonera, `btnAnadir`, `btnLimpiar`, `btnQuitar`, `btnCobrar` |
| Cambio de selección en lista | `ListSelectionListener` | `listaJuegos` |

## El EDT: por qué `SwingUtilities.invokeLater`

Swing **no es seguro para multihilo**. Existe un único hilo autorizado para tocar
la interfaz: el **EDT** (*Event Dispatch Thread*). Si construyes o modificas
componentes desde otro hilo, puedes obtener fallos intermitentes y difíciles de
reproducir.

Por eso el arranque de la aplicación delega todo al EDT:

```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new VentanaTaquilla().setVisible(true));
}
```

`invokeLater` encola la tarea en el EDT y regresa de inmediato. Ese hilo se
encarga de crear la ventana y de despachar los eventos posteriores (cada
click pasa por el EDT).

**Regla práctica:** todo lo que toque componentes Swing debe ejecutarse en el
EDT. Para tareas lentas (red, disco) se usaría un hilo aparte y se volvería al
EDT con `invokeLater` para actualizar la interfaz. En este proyecto todo es
inmediato, así que no hace falta más.

## Ciclo de vida: construir → conectar → mostrar

El constructor de `VentanaTaquilla` sigue un orden claro:

```java
public VentanaTaquilla() {
    super("Taquilla de Loterías");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1100, 700);
    setMinimumSize(new Dimension(900, 600));

    construirInterfaz();   // 1. crear y montar componentes
    conectarEventos();     // 2. registrar listeners y estado inicial
    setLocationRelativeTo(null);
}
```

| Fase | Método | Qué hace |
|------|--------|----------|
| 1. Construir | `construirInterfaz()` | Crea `Catalogo`, `Ticket` y todos los componentes; los anida |
| 2. Conectar | `conectarEventos()` | Registra listeners; llama `alCambiarJuego()` y `refrescarTicket()` |
| 3. Mostrar | `main` → `setVisible(true)` | Hace visible la ventana ya configurada |

Separar construir de conectar evita un error clásico: registrar un listener
sobre un componente que todavía es `null`, o disparar un evento antes de que el
estado esté listo.

## Siguiente

Ahora que conoces los fundamentos, entra al código del Modelo:
[Recorrido del Modelo](05-modelo-linea-a-linea.md).
