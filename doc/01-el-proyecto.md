# El proyecto

**Taquilla de Loterías** es una aplicación de escritorio en Java Swing que
recrea el flujo de venta de una taquilla de lotería real: el taquillero elige un
juego, un horario y una opción, define un monto, añade la jugada al ticket y
finalmente lo cobra. Es un proyecto educativo de nivel intermedio: no busca ser
un producto, busca mostrar cómo se organiza una aplicación de escritorio con
Modelo, Vista y Controlador.

## De dónde viene

La taquilla original es una aplicación web: el frontend se construyó con
**Astro** y se empaquetó como aplicación de escritorio con **Electron**, y los
datos se servían desde una **API REST**. Es decir, el negocio vivía en un
servidor y la interfaz en un cliente ligero.

Esta versión en Java **no es un port 1:1**. Recrea únicamente el **flujo de
venta** y lo hace enteramente en memoria, dentro de un único proceso. La
intención es aislar la lógica de negocio y la interfaz para poder estudiarlas
sin la complejidad de una red.

## Qué NO se recreó y por qué

| Se dejó fuera | Por qué |
|---------------|---------|
| Backend y API REST | El objetivo es estudiar POO, MVC y Swing, no redes ni contratos HTTP. |
| Persistencia (base de datos o JSON) | Las jugadas viven en memoria mientras la app está abierta. Reiniciar borra el ticket. |
| Autenticación y usuarios | No hay login ni roles; la app asume un solo taquillero. |
| Pagos y facturación | "Cobrar" solo muestra el resumen del ticket; no hay pasarela ni impresión. |
| Sincronización multiusuario | No hay concurrencia real de taquillas; un solo proceso controla el estado. |
| Electron y Astro | Se sustituyen por un `JFrame` con Swing. |

> El comentario de `Catalogo` lo dice sin rodeos: *"En un entorno de producción
> esta información vendría de un archivo JSON o de una API externa; aquí se
> carga directamente en el constructor para simplificar el proyecto educativo."*

## Flujo de negocio, paso a paso

1. **Elegir el juego.** La lista de la izquierda muestra los juegos del catálogo.
2. **Elegir el horario.** El combo se rellena con los horarios del juego elegido.
3. **Elegir la opción.** El tablero central muestra las opciones del juego como botones.
4. **Definir el monto.** El `JSpinner` arranca en 10,00 Bs.
5. **Añadir la jugada.** Se valida la selección y se agrega una `Jugada` al `Ticket`.
6. **Repetir** los pasos 2–5 tantas veces como jugadas tenga el ticket.
7. **Quitar** una jugada seleccionada de la tabla, si hace falta.
8. **Cobrar el ticket.** Se muestra el texto del ticket y, al cerrar el aviso, se limpia.

Cada paso de este flujo tiene un método concreto en `VentanaTaquilla`. El
recorrido detallado está en [Flujo de una venta](07-flujo-de-una-venta.md).

## Estructura de `src/`

```
src/
├── Modelo/                  # Reglas de negocio (sin Swing)
│   ├── Catalogo.java        # Juegos en memoria (constructor los carga)
│   ├── Juego.java           # Juego vendible + opciones + horarios + multiplicador
│   ├── Jugada.java          # Una línea del ticket (juego + horario + opción + monto)
│   ├── Opcion.java          # Un valor apostable (animalito, terminal o signo)
│   └── Ticket.java          # Acumula las jugadas en curso
└── GUI/                     # Interfaz Swing
    ├── Botonera.java        # Grilla de botones reutilizable con selección
    ├── Formato.java         # Formato de moneda en bolívares
    ├── JugadaTableModel.java# Adaptador Ticket → JTable
    ├── PanelTablero.java    # Tablero de opciones del juego activo
    ├── PanelTicket.java     # Vista del ticket y sus acciones
    └── VentanaTaquilla.java # JFrame + controlador (punto de entrada)
```

La separación es deliberada: **`Modelo` no importa `javax.swing` en ningún
punto**. Ese límite se explica en [Arquitectura MVC](03-arquitectura-mvc.md).

## Los tres juegos del catálogo

Datos tomados de `Catalogo.cargar()`:

| Juego | `id` | `tipo` | Multiplicador | Opciones | Horarios |
|-------|------|--------|---------------|----------|----------|
| Lotto Activo | 1 | `animalitos` | 30 | 38 | 12 (08:00 a 19:00) |
| Terminal Activo | 3 | `terminales` | 60 | 100 ("00" a "99") | 12 (08:00 a 19:00) |
| Triple Zulia | 2 | `tripletas` | 600 | 12 signos zodiacales | 3 (12:45, 16:45, 19:05) |

Dos detalles que conviene fijar desde ahora:

- **Los `id` no siguen el orden de la lista.** Lotto Activo es 1, Triple Zulia es 2
  y Terminal Activo es 3, aunque en el catálogo se cargan Lotto, Terminal y Triple.
  El orden visible en la interfaz lo decide el orden de inserción, no el `id`.
- **Lotto Activo tiene 38 opciones, no 37.** Ballena y Delfín comparten el número `0`,
  y los animales llegan hasta el `36` (Culebra).

## Siguiente

Ahora que conoces el alcance, pasa a [POO en Java](02-poo-en-java.md) para ver
cómo estas reglas de negocio se expresan con clases, objetos y encapsulamiento.
