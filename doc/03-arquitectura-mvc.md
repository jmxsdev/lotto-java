# Arquitectura Modelo / Vista / Controlador

**La respuesta corta:** el **Modelo** guarda las reglas y los datos, la **Vista**
dibuja y captura clics, y el **Controlador** recibe esos clics y decide qué hacer
con el Modelo y qué pedirle a la Vista. En este proyecto la separación es
estricta: el Modelo no conoce Swing.

## El problema de mezclar todo

Si el código de la taquilla viviera en una sola clase, cada botón tendría lógica
de negocio, formato y validación enredados. Consecuencias típicas:

- No puedes probar el cálculo del premio sin abrir una ventana.
- Cambiar un color o un texto obliga a tocar lógica de negocio.
- Un cambio en el catálogo puede romper un evento de la interfaz.

Separar en capas hace que cada cambio afecte a un solo lugar.

## Las tres capas y su regla

| Capa | Paquete | Responsabilidad | No debe |
|------|---------|-----------------|---------|
| Modelo | `Modelo` | Datos y reglas del negocio | Importar `javax.swing` ni conocer la GUI |
| Vista | `GUI` (paneles) | Dibujar y avisar de la interacción | Decidir reglas de negocio |
| Controlador | `VentanaTaquilla` | Coordinar Modelo y Vista | Duplicar validaciones del Modelo |

## Diagrama del flujo

```
        ┌───────────────────────────────────────────────┐
        │                  Modelo                        │
        │  Catalogo / Juego / Opcion / Jugada / Ticket   │
        │  (reglas, validaciones, sin javax.swing)       │
        └───────────────────────▲───────────────────────┘
                                │ usa y consulta
                                │
        ┌───────────────────────┴───────────────────────┐
        │             VentanaTaquilla (JFrame)           │
        │                CONTROLADOR                     │
        │  conectarEventos · anadirJugada · cobrar...    │
        └───────▲───────────────────────────────┬───────┘
                │ eventos (callbacks)           │ mostrar / actualizar
                │                               ▼
        ┌───────┴───────────────────────────────────────┐
        │                    Vista                       │
        │  PanelTablero · PanelTicket · Botonera · JTable│
        │  (Swing: dibuja y captura clics)               │
        └───────────────────────────────────────────────┘
```

El flujo siempre es el mismo: la **Vista** avisa al **Controlador**, el
**Controlador** actúa sobre el **Modelo** y luego ordena a la **Vista**
refrescarse.

## Clases y su rol

| Clase | Rol | Capa |
|-------|-----|------|
| `Catalogo` | Fuente de los juegos disponibles | Modelo |
| `Juego` | Juego vendible y sus reglas de pago | Modelo |
| `Opcion` | Valor apostable y su etiqueta | Modelo |
| `Jugada` | Apuesta registrada | Modelo |
| `Ticket` | Colección de jugadas en curso | Modelo |
| `PanelTablero` | Muestra opciones y notifica la selección | Vista |
| `PanelTicket` | Muestra la tabla y expone acciones | Vista |
| `Botonera` | Grilla de botones reutilizable | Vista |
| `Formato` | Formato de moneda | Utilidad de vista |
| `JugadaTableModel` | Adaptador `Ticket` → `JTable` | Vista (adaptador) |
| `VentanaTaquilla` | Ensambla todo y coordina el flujo | Controlador |

## La regla de oro: el Modelo no conoce Swing

Esta es la regla que sostiene toda la arquitectura. En el paquete `Modelo` no
aparece ni una sola importación de `javax.swing`.

**Cómo comprobarlo tú mismo:**

```bash
grep -rn "javax.swing" src/Modelo
```

Si el comando no imprime nada, la regla se cumple. Y así es: `Modelo` solo
importa `java.util.ArrayList`, `java.util.List` y `java.util.Collections`.

¿Por qué importa? Porque un `Ticket` que sabe pintar botones es un `Ticket` que
no puedes reutilizar sin arrastrar toda la interfaz. Al mantener el Modelo
limpio, el negocio se puede probar, versionar y hasta migrar a otra tecnología
de interfaz (o a la API REST original) sin tocarlo.

## Por qué las vistas usan callbacks y no el controlador

`PanelTicket` **no recibe** un `VentanaTaquilla`. Recibe dos `Runnable`:

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

El controlador conecta así:

```java
panelTicket.setAlQuitar(this::quitarJugada);
panelTicket.setAlCobrar(this::cobrarTicket);
```

Ventajas de este patrón (a veces llamado *inversión de dependencias*):

- `PanelTicket` no sabe quién lo usa; puede reutilizarse en cualquier ventana.
- Se puede probar el panel con un `Runnable` cualquiera, sin crear un `JFrame`.
- La dirección de la dependencia va de la vista al contrato, no al revés.

`PanelTablero` ofrece el mismo tipo de hueco, pero con argumento:

```java
private Consumer<Opcion> alSeleccionar;
```

`VentanaTaquilla` sí lo registra, y con él habilita el botón "Añadir jugada"
solo cuando hay una opción elegida:

```java
panelTablero.setAlSeleccionar(opcion -> btnAnadir.setEnabled(opcion != null));
```

Así el panel no necesita saber que existe un botón, y el controlador no necesita
consultar el tablero en cada momento: la vista avisa cuando cambia la selección.
Ver el recorrido en [GUI línea a línea](06-gui-linea-a-linea.md).

## Matiz honesto: el `JFrame` es el controlador

En MVC "puro", el controlador es una clase separada del contenedor visual. Aquí
`VentanaTaquilla` **es a la vez** el `JFrame` y el controlador:

```java
public class VentanaTaquilla extends JFrame {
    // campos del controlador: catalogo, ticket, vistas...
}
```

¿Es incorrecto? No necesariamente. Es un **compromiso aceptable en una
aplicación pequeña**: hay una sola ventana, un solo flujo y el controlador se
beneficia de acceder directamente a los componentes. Separar un
`TaquillaController` de un `TaquillaView` daría más pureza a cambio de más
indirección.

Lo que **sí** se respeta es la regla importante: las **vistas** siguen sin conocer
el Modelo salvo para dibujarlo, y el **Modelo** sigue sin conocer Swing. La
deuda arquitectónica está localizada en un único punto (el `JFrame`), no
repartida por todo el proyecto.

## Siguiente

Con la separación clara, sigue [Swing: fundamentos](04-swing-fundamentos.md)
para entender los componentes, layouts y el hilo de la interfaz que usan esas
vistas.
