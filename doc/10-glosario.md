# Glosario

**La respuesta corta:** definiciones breves de los términos que aparecen en el
manual, con el lugar del proyecto donde tienen vida. Úsalo como referencia, no
como lectura lineal.

## POO

| Término | Significado breve | Dónde aparece en el proyecto |
|---------|-------------------|------------------------------|
| Clase | Molde que define campos y comportamiento | `Opcion`, `Juego`, `Jugada`, `Ticket`, `Catalogo` |
| Objeto | Instancia concreta creada a partir de una clase | `new Opcion(0, "Ballena", "ballena", null)` |
| Instancia | Sinónimo de objeto; "una instancia de `Juego`" | Cada juego de `Catalogo.cargar()` |
| Estado | Valores guardados en los campos de un objeto | `opciones`, `horarios`, `premioMultiplo` de `Juego` |
| Comportamiento | Lo que un objeto sabe hacer (sus métodos) | `Opcion.getEtiquetaTablero()`, `Ticket.getTotal()` |
| Encapsulamiento | Ocultar campos y exponer acceso controlado | Campos `private final` + getters en `Juego` |
| Inmutabilidad | El objeto no cambia tras construirse | `Opcion`, `Jugada` (clase `final`, campos `final`, sin setters) |
| Copia defensiva | Copiar al recibir y devolver vistas no modificables | Constructor de `Juego`, `getJugadas()`, `getJuegos()` |
| Composición ("tiene un") | Un objeto contiene a otro | `Ticket` tiene `List<Jugada>`; `Jugada` tiene `Juego` y `Opcion` |
| Herencia ("es un") | Una clase deriva de otra | `JugadaTableModel extends AbstractTableModel` |
| Clase abstracta | Clase base con métodos por completar | `AbstractTableModel` |
| Contrato | Acuerdo sobre *qué* promete algo, separado de *cómo* lo hace por dentro. No es una palabra clave de Java: es vocabulario de diseño | Interfaz `Runnable`; `AbstractTableModel`; el `Consumer<Opcion>` de `setAlSeleccionar` |
| Interfaz | Contrato de métodos, sin implementación | `ActionListener`, `Consumer<Opcion>`, `Runnable` |
| Interfaz funcional | Interfaz con un solo método abstracto | `Runnable` (`run`), `Consumer` (`accept`), `ActionListener` (`actionPerformed`) |
| Polimorfismo | Un mismo mensaje se resuelve según el objeto | `toString()` en `Opcion`/`Juego`; `getValueAt` en `JugadaTableModel` |
| Sobrescritura (`@Override`) | Redefinir un método heredado | `toString`, `getValueAt`, `getRowCount` |
| Delegación | Un objeto pide a otro que haga el trabajo | `Jugada.getPremioPotencial()` llama a `Juego.premioPotencial` |
| Excepción | Error señalado en tiempo de ejecución | `IllegalArgumentException` en constructores; `IndexOutOfBoundsException` en `Ticket.quitar` |
| Validación temprana | Rechazar datos inválidos al construir | Checks al inicio de cada constructor del Modelo |
| `final` en campos | El campo se asigna una sola vez | Prácticamente todos los campos del Modelo |
| `final` en clases | La clase no puede heredarse | `Opcion`, `Juego`, `Jugada`, `Ticket`, `Formato` |

## Swing y escritorio

| Término | Significado breve | Dónde aparece en el proyecto |
|---------|-------------------|------------------------------|
| AWT | Base de ventanas y eventos del JDK | Usado indirectamente por Swing (`BorderLayout`, `Color`) |
| Swing | Toolkit de componentes de escritorio | Todo el paquete `GUI` |
| Componente | Elemento visual (botón, etiqueta, tabla…) | `JButton`, `JLabel`, `JTable`, `JSpinner` |
| Contenedor | Componente que agrupa a otros | `JFrame`, `JPanel` |
| `contentPane` | Área interior del `JFrame` | `VentanaTaquilla.construirInterfaz()` |
| Layout manager | Decide la posición de los hijos de un contenedor | `BorderLayout`, `GridLayout`, `FlowLayout` |
| `BorderLayout` | Zonas NORTE/SUR/ESTE/OESTE/CENTRO | Estructura general de `VentanaTaquilla` |
| `GridLayout` | Rejilla de celdas iguales | `Botonera` (tablero de opciones) |
| `FlowLayout` | Fila que fluye y salta de línea | `panelMonto` y los botones del ticket |
| Evento | Acción del usuario que el sistema notifica | Clic en botón, cambio de selección en lista |
| Listener | Objeto que reacciona a un evento | `ActionListener`, `ListSelectionListener` |
| Lambda | Forma corta de implementar una interfaz funcional | `e -> anadirJugada()`, `this::cobrarTicket` |
| `ActionListener` | Listener de clics en botones | `Botonera`, `btnAnadir`, `btnLimpiar`, `btnCobrar`, `btnQuitar` |
| `ListSelectionListener` | Listener de selección en listas | `listaJuegos` en `conectarEventos()` |
| EDT (Event Dispatch Thread) | Único hilo autorizado para tocar Swing | `SwingUtilities.invokeLater` en `main` |
| `invokeLater` | Encola una tarea en el EDT | Arranque de `VentanaTaquilla` |
| Callback | Hueco de comportamiento que alguien registra | `Consumer<Opcion>` en `PanelTablero`; `Runnable` en `PanelTicket` |
| Modelo de tabla | Objeto que da filas y columnas a un `JTable` | `JugadaTableModel extends AbstractTableModel` |
| `fireTableDataChanged()` | Avisa a la tabla de que los datos cambiaron | `JugadaTableModel.setJugadas` |
| `JScrollPane` | Añade barras de desplazamiento a un componente | Envuelve la tabla, la lista de juegos y el tablero |
| `JOptionPane` | Diálogo modal estándar | `mostrarAviso` y `cobrarTicket` |
| Layout inválido / `revalidate`+`repaint` | Recalcular y repintar tras cambiar componentes | `PanelTablero.mostrar` |
| `HeadlessException` | Error al usar GUI sin entorno gráfico | Se explica en [Ejecutar y depurar](08-ejecutar-y-depurar.md) |
| Fuente monoespaciada | Fuente de ancho fijo | Texto del ticket (`Font.MONOSPACED`) |
| Formato localizado | Números según la región | `Formato` con `Locale.of("es","VE")` |

## Siguiente

Fin del manual. Vuelve al [Índice](README.md) para repasar la ruta o elegir
otro documento.
