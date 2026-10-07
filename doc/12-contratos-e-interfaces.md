# Contratos: interfaces y clases abstractas

**La respuesta corta:** un **contrato** es el acuerdo sobre *qué* promete algo,
sin decir *cómo* lo hace. En Java se expresa de dos formas: con una **interfaz**
(cuando solo defines el *qué*) o con una **clase abstracta** (cuando ya dejas
parte del *cómo* hecho y solo faltan huecos por completar).

## Qué es un contrato (recordatorio)

El glosario lo define como *"acuerdo sobre qué promete algo, separado de cómo lo
hace por dentro"*. Como un enchufe: te prometen "entra voltaje por estos dos
pines", y no te importa si dentro hay carbón o energía nuclear.

Quien **usa** algo depende del contrato; quien lo **implementa** puede cambiar sus
tripas mientras cumpla la promesa. Ese es todo el truco.

## Las dos formas de expresar un contrato

| | Interfaz | Clase abstracta |
|---|---|---|
| Qué es | Lista de métodos a cumplir | Clase a medio hacer |
| Estado (campos de instancia) | No; solo constantes (`static final`) | Sí, puede tenerlos |
| Código ya implementado | No (salvo métodos `default`) | Sí, métodos completos |
| Cuántas puedes usar | Una clase implementa **varias** | Una clase extiende **una sola** |
| Palabra clave | `implements` | `extends` |
| Cuándo elegirla | Solo defines el **qué** | Ya dejas parte del **cómo** resuelta |

## En este proyecto

### Interfaces: solo el qué

| Interfaz | Su único método | Se usa como |
|----------|-----------------|-------------|
| `ActionListener` | `actionPerformed(ActionEvent)` | Listener de botones |
| `Consumer<Opcion>` | `accept(Opcion)` | Callback del tablero |
| `Runnable` | `run()` | Acciones de `PanelTicket` |

Ninguna dice *cómo* se cobra un ticket ni *qué* se hace al elegir una opción.
Solo dicen *"me comprometo a tener este método"*.

### Clase abstracta: parte hecha, parte por completar

`AbstractTableModel` es el contrato de "modelo de tabla", pero a diferencia de
una interfaz **ya trae trabajo hecho**: el manejo de listeners de la tabla y los
métodos `fireTableDataChanged()`, `fireTableRowsInserted()`, etc. Lo único que
deja sin resolver son tres huecos:

```
                  contrato "modelo de tabla"
                            │
              AbstractTableModel   ← parte hecha + 3 huecos
                            │ extends
               JugadaTableModel    ← rellena los 3 huecos
```

Esos tres huecos son `getRowCount`, `getColumnCount` y `getValueAt`. Hasta que no
los implements, `JugadaTableModel` no compila. Eso es un contrato **exigido por el
compilador**, no por buena voluntad.

## Interfaz funcional y lambda

Una **interfaz funcional** es una interfaz con **un solo método abstracto**. Eso
permite escribirla como *lambda*, sin crear una clase:

| Contrato | Sin lambda | Con lambda |
|----------|-----------|------------|
| `Runnable` | `new Runnable() { public void run() { ... } }` | `() -> cobrarTicket()` |
| `Consumer<Opcion>` | clase anónima que implementa `accept` | `opcion -> btnAnadir.setEnabled(opcion != null)` |
| `ActionListener` | clase anónima que implementa `actionPerformed` | `e -> anadirJugada()` |

Mira cómo el proyecto las usa de verdad en
[Arquitectura MVC](03-arquitectura-mvc.md).

## "Programa hacia la interfaz, no hacia la implementación"

Compara estas dos versiones de `PanelTicket`:

```java
// Acoplado: el panel conoce al controlador concreto
public void setAlCobrar(VentanaTaquilla v) { ... }

// Contrato: el panel solo conoce "algo con run()"
public void setAlCobrar(Runnable r) { ... }
```

En la primera, `PanelTicket` queda **atado** a `VentanaTaquilla`: no lo puedes
reutilizar en otra ventana ni probarlo sin construir la ventana entera. En la
segunda, depende de un contrato y acepta cualquier cosa que cumpla la promesa.
Esa es la *inversión de dependencias*: la flecha apunta al contrato, no a la clase
real.

## Cómo elegir (regla práctica)

1. ¿Solo defines comportamiento, sin estado ni código compartido? → **interfaz**.
2. ¿Necesitas compartir código base *y* obligar a completar huecos? → **clase abstracta**.
3. ¿Dudas? → empieza por **interfaz**; convertir después a clase abstracta es más
   fácil que al revés.

## Errores comunes

| Error | Qué ocurre |
|-------|-----------|
| Confundir `implements` (interfaz) con `extends` (clase) | No compila |
| Olvidar implementar un método abstracto | El compilador lo exige (`JugadaTableModel` no compilaría) |
| Intentar guardar estado en una interfaz | No se puede: solo constantes `static final` |
| Creer que una interfaz "hereda" de una clase | No; las interfaces se *implementan* |

## Checklist

- [ ] ¿Sé distinguir cuándo el contrato pide solo el *qué* (interfaz) y cuándo trae parte del *cómo* (clase abstracta)?
- [ ] ¿Entiendo por qué `setAlCobrar(Runnable)` desacopla y `setAlCobrar(VentanaTaquilla)` no?
- [ ] ¿Reconozco una interfaz funcional y por eso se puede escribir como lambda?
- [ ] ¿Sé que el compilador obliga a cumplir el contrato de una clase abstracta?

## Siguiente

Para ver los contratos aplicados al flujo completo, vuelve a
[Arquitectura MVC](03-arquitectura-mvc.md). Si quieres repasar antes los
conceptos de POO, ve a [POO en Java](02-poo-en-java.md).
