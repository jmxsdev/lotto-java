# El click completo: flujo de una venta

**La respuesta corta:** agregar una jugada ocurre en **dos clicks**. El primero
marca la opción en el tablero; el segundo, pulsar "Añadir jugada", construye la
`Jugada`, la mete en el `Ticket` y refresca la tabla. Aquí se sigue ese recorrido
nombrando cada método.

## Diagrama de secuencia

```
Taquillero
   │  (1) click en un botón del tablero
   ▼
JButton  ── ActionListener (lambda) ──▶  PanelTablero.seleccionar(indice)
                                              │
                                              ├─ seleccionada = juego.getOpciones().get(indice)
                                              ├─ botonera.marcarSeleccion(indice)
                                              │      └─ borra borde anterior, pinta borde azul
                                              └─ alSeleccionar.accept(opcion)  → habilita "Añadir jugada"

Taquillero
   │  (2) click en "Añadir jugada"
   ▼
JButton btnAnadir ── ActionListener ──▶ VentanaTaquilla.anadirJugada()
                                              │
                                              ├─ listaJuegos.getSelectedValue()
                                              ├─ panelTablero.getSeleccionada()
                                              ├─ comboHorarios.getSelectedItem()
                                              ├─ spinnerMonto.getValue()
                                              │
                                              ├─ new Jugada(juego, horario, opcion, monto)
                                              ├─ ticket.agregar(jugada)
                                              ├─ refrescarTicket()
                                              │      ├─ ticket.getJugadas()
                                              │      ├─ ticket.getTotal()
                                              │      └─ panelTicket.actualizar(jugadas, total)
                                              │            ├─ modelo.setJugadas(jugadas)
                                              │            │     └─ fireTableDataChanged()
                                              │            │           └─ JTable.getValueAt(fila, col) por celda
                                              │            └─ setEnabled(...) en los botones
                                              └─ panelTablero.limpiarSeleccion()
```

El paso (1) no crea ninguna jugada: solo deja la opción marcada y avisa por el
callback `alSeleccionar`, que `VentanaTaquilla` usa para habilitar "Añadir
jugada". La jugada se construye en el paso (2), que además lee la opción con
`getSeleccionada()`.

## Paso a paso

| Paso | Quién actúa | Qué ocurre |
|------|-------------|------------|
| 1 | `JButton` del tablero | Se dispara el `ActionListener` registrado en `PanelTablero.mostrar()` |
| 2 | `PanelTablero.seleccionar` | Guarda `seleccionada`, llama a `marcarSeleccion` y notifica al callback (que habilita "Añadir jugada") |
| 3 | `Botonera.marcarSeleccion` | Quita el borde al botón anterior y pinta el borde azul al nuevo |
| 4 | `JButton` "Añadir jugada" | Dispara `anadirJugada()` |
| 5 | `VentanaTaquilla.anadirJugada` | Lee juego, opción, horario y monto; valida que ninguno falte |
| 6 | `VentanaTaquilla.anadirJugada` | Crea `new Jugada(...)` y la agrega con `ticket.agregar(...)` |
| 7 | `VentanaTaquilla.refrescarTicket` | Pide jugadas y total al `Ticket` y llama a `panelTicket.actualizar(...)` |
| 8 | `PanelTicket.actualizar` | Delega en `modelo.setJugadas(...)` y actualiza el total; habilita botones |
| 9 | `JugadaTableModel.setJugadas` | Copia la lista y dispara `fireTableDataChanged()` |
| 10 | `JTable.getValueAt` | Pide celda por celda: nombre, horario, etiqueta, monto, premio |
| 11 | `VentanaTaquilla.anadirJugada` | Llama a `panelTablero.limpiarSeleccion()` para dejar el tablero listo |

Si en el paso 5 falta algo, `anadirJugada` muestra un aviso con `mostrarAviso` y
hace `return`: la jugada no se crea.

## Caso "Cobrar ticket"

El botón "Cobrar ticket" se conecta en `conectarEventos()` con
`panelTicket.setAlCobrar(this::cobrarTicket)`. Al pulsarlo:

```
click "Cobrar ticket"
   ▼
PanelTicket (Runnable alCobrar)  ──▶  VentanaTaquilla.cobrarTicket()
                                          │
                                          ├─ ticket.getCantidad() == 0 ?
                                          │      └─ sí → mostrarAviso("No hay jugadas...") y salir
                                          │
                                          ├─ construirTextoTicket()      ← arma el texto con StringBuilder
                                          ├─ new JTextArea(texto)        ← fuente monoespaciada
                                          ├─ new JScrollPane(area)       ← 480 × 320
                                          ├─ JOptionPane.showMessageDialog(...)   ← bloquea hasta cerrar
                                          ├─ ticket.limpiar()
                                          └─ refrescarTicket()
```

`construirTextoTicket()` recorre `ticket.getJugadas()` y por cada una escribe
juego, horario, etiqueta, monto, premio y multiplicador, con separadores de `=`
y `-`. Al final añade el total y la cantidad.

El diálogo del `JOptionPane` es **modal**: el método no continúa hasta que el
usuario lo cierra. Justo después, el ticket se limpia. Es importante notar que
`cobrarTicket` **no** limpia la selección del tablero; eso solo ocurre en
`limpiarTicket()` (el botón "Limpiar ticket") y en `anadirJugada()`.

## Preguntas de comprensión

1. ¿Por qué la jugada se crea al pulsar "Añadir jugada" y no al pulsar la opción
   en el tablero? ¿Qué estado intermedio guarda `PanelTablero`?
2. ¿Qué pasaría si `refrescarTicket()` no se llamara después de
   `ticket.agregar(...)`? Relaciona tu respuesta con `fireTableDataChanged()`.
3. Si `PanelTablero.limpiarSeleccion()` no llamara a `notificarSeleccion()`, ¿en
   qué estado quedaría el botón "Añadir jugada" después de añadir una jugada?

## Siguiente

Para ver todo esto funcionando en tu máquina, sigue
[Ejecutar y depurar](08-ejecutar-y-depurar.md).
