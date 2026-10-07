# Ejecutar y depurar

**La respuesta corta:** abre el proyecto en NetBeans y pulsa **F6**, o compila y
ejecuta desde la terminal con los comandos de abajo. Este documento también
resuelve los errores más comunes y explica cómo depurar.

## Con NetBeans

1. `File → Open Project…` y selecciona la carpeta `lotto-java`.
2. NetBeans detecta el proyecto Ant (`nbproject/`).
3. Pulsa **F6** (Run Project).

La clase principal ya está configurada en `nbproject/project.properties`:

```
main.class=GUI.VentanaTaquilla
```

Por eso F6 lanza `GUI.VentanaTaquilla` sin configuración extra.

## Con la terminal

Desde la raíz del proyecto (`lotto-java/`), compila y ejecuta:

```bash
javac -encoding UTF-8 -d build/classes $(find src -name '*.java')
java -cp build/classes GUI.VentanaTaquilla
```

- `-encoding UTF-8` evita que los acentos de "Delfín", "Águila" o "Camello" se
  corrompan al compilar.
- `-d build/classes` coloca los `.class` en la misma carpeta que usa el proyecto.
- `$(find src -name '*.java')` compila todos los fuentes de una sola vez. Requiere
  Bash o Zsh (la sintaxis `$(...)` es de shell).
- `-cp build/classes` le dice a la JVM dónde encontrar las clases; el argumento
  final es el nombre completo de la clase, no una ruta.

**Requisito de versión:** el código usa `Locale.of("es","VE")`, disponible desde
Java 19, y el proyecto declara `javac.source=27` / `javac.target=27`. Compila con
**Java 19 o superior** (en este equipo hay Java 27). Con un JDK más antiguo,
`Locale.of` no existe y la compilación fallará en `Formato.java`.

> **No uses `ant` si no lo tienes instalado.** NetBeans y algunos tutoriales
> asumen `ant`, pero puede no estar presente. Los dos comandos de arriba son
> suficientes y no requieren nada más que el JDK.

## Clases `.class` y archivos `.jar`

**La respuesta corta:** para *ejecutar* no necesitas un `.jar`. Compilar genera
archivos `.class`, y esos ya se pueden ejecutar. El `.jar` sirve para
**empaquetar** toda la aplicación en un solo archivo y poder distribuirla.

Son tres pasos distintos, con herramientas distintas:

| Paso | Herramienta | Entrada | Salida |
|------|-------------|---------|--------|
| Compilar | `javac` | fuentes `.java` | bytecode `.class` (uno por clase) |
| Empaquetar | `jar` | `.class` + un manifiesto | un único `.jar` |
| Ejecutar | `java` | `.class` o `.jar` | la aplicación en marcha |

Un `.jar` **no es bytecode nuevo**: es un archivo ZIP que agrupa los `.class`,
los recursos y una carpeta `META-INF/` con el manifiesto. Por eso estas dos
órdenes arrancan exactamente lo mismo:

```bash
java -cp build/classes GUI.VentanaTaquilla      # clases sueltas
java -jar dist/Taquilla.jar                     # las mismas clases empaquetadas
```

### Construir el `.jar`

Desde la raíz del proyecto, después de compilar:

```bash
jar --create --file dist/Taquilla.jar \
    --main-class GUI.VentanaTaquilla \
    -C build/classes .
```

- `--create --file dist/Taquilla.jar` → crea el archivo en `dist/`.
- `--main-class GUI.VentanaTaquilla` → escribe `Main-Class:` en el manifiesto.
- `-C build/classes .` → empaqueta todo lo que hay dentro de `build/classes`.

### Ejecutarlo

```bash
java -jar dist/Taquilla.jar                       # usa el Main-Class del manifiesto
java -cp dist/Taquilla.jar GUI.VentanaTaquilla    # le dices tú la clase a arrancar
```

### Por qué importa el `Main-Class`

Un `.jar` **sin** `Main-Class` falla así:

```
no main manifest attribute, in Taquilla.jar
```

Es la JVM diciendo: "abrí el ZIP, pero no sé qué clase tiene el `main`". La
segunda forma (`java -cp ... paquete.Clase`) seguiría funcionando, porque ahí la
clase la indicas tú. El manifiesto solo es obligatorio para el atajo `java -jar`.

> **Con NetBeans** no hace falta ejecutar `jar` a mano: el botón **Build**
> compila y genera `dist/Taquilla.jar` (ya configurado en
> `nbproject/project.properties`). Ese archivo es el que copiarías a otra
> máquina para ejecutar con `java -jar`.

¿Dudas sobre `-cp` y de dónde salen los errores de "class not found"? Están
explicados con detalle en [El classpath](11-classpath.md).

## Errores comunes y su causa

| Síntoma | Causa probable | Solución |
|---------|----------------|----------|
| `java.awt.HeadlessException` | No hay entorno gráfico disponible (SSH sin `DISPLAY`, servidor, CI) | Ejecuta en una sesión de escritorio; si es remoto, habilita X11 forwarding (`ssh -X`) o usa un escritorio remoto |
| `Can't connect to X11 window server using ':0'` | La variable `DISPLAY` apunta a un servidor gráfico inexistente | Corrige/exporta `DISPLAY`, o ejecuta localmente |
| Emoji que no se ve (aparece un cuadro vacío) | La fuente del sistema no tiene esos glifos | Instala una fuente con emoji (p. ej. Noto) o usa una fuente embebida; ver el ejercicio correspondiente en [Ejercicios](09-ejercicios.md) |
| `ant: command not found` | NetBeans/tutorial espera Ant y no está instalado | Usa los comandos `javac`/`java` de arriba |
| La app corre con comportamiento viejo | Olvidaste recompilar tras editar | Vuelve a ejecutar `javac ...` antes de `java ...` |
| `no main manifest attribute, in X.jar` | El `.jar` no tiene `Main-Class` en el manifiesto | Créalo con `--main-class`, o ejecútalo con `java -cp X.jar paquete.Clase` |
| `Error: Unable to access jarfile X.jar` | La ruta del `.jar` está mal o no existe | Comprueba la ruta (p. ej. `dist/Taquilla.jar`) y que hayas empaquetado antes |
| `error: package Modelo does not exist` | Compilaste desde una carpeta equivocada o no incluiste `src` | Ejecuta el comando desde la raíz del proyecto |
| Los acentos se ven mal | Compilaste sin `-encoding UTF-8` | Añade el flag y recompila |

## Cómo depurar

### Con `System.out.println`

El método más rápido para entender el estado. Colócalo temporalmente y observa la
consola:

```java
System.out.println("jugadas=" + ticket.getCantidad() + " total=" + ticket.getTotal());
```

Dos consejos:

- Imprime **valores del Modelo**, no textos genéricos. `ticket.getJugadas()` te
  dice exactamente qué hay.
- Elimínalos antes de dar por terminado un cambio. El repositorio no debería
  quedar lleno de trazas.

### Con breakpoints en NetBeans

1. Haz clic en el margen, junto al número de línea, para colocar un breakpoint.
2. Lanza **Debug Project** (en el mapping por defecto de NetBeans es `F5`; los
   atajos pueden variar según tu keymap).
3. Cuando la ejecución se detenga, inspecciona variables. Los puntos de entrada
   útiles suelen ser `anadirJugada`, `cobrarTicket` y `JugadaTableModel.getValueAt`.

Pasos típicos de la barra de depuración: *Step Into*, *Step Over*, *Continue* y
*Run to Cursor*. Úsalos para entrar en `ticket.agregar(...)` y ver cómo cambia la
lista interna.

### Qué mirar según el síntoma

| Síntoma | Punto de inspección |
|---------|---------------------|
| La jugada no se añade | `anadirJugada`: ¿opción `null`? ¿monto `<= 0`? |
| La tabla no cambia | `refrescarTicket` → `JugadaTableModel.setJugadas` |
| El premio no cuadra | `Juego.premioPotencial` y el multiplicador del juego |
| El horario está vacío | `alCambiarJuego` y `comboHorarios` |
| El botón está deshabilitado | `PanelTicket.actualizar` y `jugadas.isEmpty()` |

## Checklist final

Si algo no funciona, revisa en este orden:

- [ ] ¿Estás en la raíz del proyecto (`lotto-java/`) al compilar?
- [ ] ¿Compilaste **después** del último cambio? (`javac ...`)
- [ ] ¿Usaste `-encoding UTF-8`?
- [ ] ¿Tu JDK es 19 o superior?
- [ ] ¿Hay un entorno gráfico disponible (`DISPLAY` correcto)?
- [ ] ¿Añadiste el juego/opción tanto al `Catalogo` como a donde se consume?
- [ ] ¿El `JugadaTableModel` dispara `fireTableDataChanged()` tras cambiar datos?
- [ ] ¿Revisaste la consola por si hay una excepción antes del fallo visible?

## Siguiente

Con la aplicación corriendo, es hora de modificarla:
[Ejercicios](09-ejercicios.md).
