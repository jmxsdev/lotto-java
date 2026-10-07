# El classpath (`-cp`): dónde busca Java tus clases

**La respuesta corta:** el *classpath* es la **lista de sitios donde `javac` y
`java` buscan los archivos `.class`**. Se la das con `-cp`; si no la das, Java usa
el **directorio actual**. Sin una entrada correcta aparece `class not found`.

## Por qué existe

Un `.exe` suele ser un bloque único y autocontenido. Una aplicación Java no: son
**muchos `.class`** que la JVM va cargando **bajo demanda**. Cuando el programa
necesita `Modelo.Ticket`, la JVM:

1. Toma el nombre: `Modelo.Ticket`.
2. Lo convierte en ruta: `Modelo/Ticket.class`.
3. Busca ese archivo en **cada entrada del classpath, en orden**.
4. Si no lo encuentra en ninguna, lanza un error.

El classpath no es una "variable misteriosa": es, literalmente, **dónde mirar**.

## De nombre de clase a ruta

Los puntos del paquete se convierten en carpetas. El classpath es la **raíz** desde
donde se cuentan:

```
classpath:  -cp build/classes
clase:      GUI.VentanaTaquilla
                        ↓ se unen
archivo:    build/classes/GUI/VentanaTaquilla.class
```

Por eso `-cp build` **no** sirve para `GUI.VentanaTaquilla`: buscaría
`build/GUI/...`, que no existe. Verificado en este proyecto:

```bash
$ javap -cp build/classes GUI.VentanaTaquilla
Compiled from "VentanaTaquilla.java"
public class GUI.VentanaTaquilla extends javax.swing.JFrame {

$ javap -cp build GUI.VentanaTaquilla
Error: class not found: GUI.VentanaTaquilla
```

> `javap` es la herramienta del JDK para inspeccionar `.class`. Con `-cp` es la
> forma más rápida de comprobar **si Java encuentra una clase** sin ejecutarla.

## Las tres formas de dar el classpath

| Forma | Ejemplo | Cuándo usarla |
|-------|---------|---------------|
| Opción `-cp` (o `-classpath`) | `java -cp build/classes GUI.VentanaTaquilla` | Lo normal: explícita y local al comando |
| Variable `CLASSPATH` | `export CLASSPATH=build/classes` | Rara vez; es global y fácil de olvidar |
| Por defecto | (no pasar nada) | Vale `.` (el directorio actual) |

**Regla que confunde:** si pasas `-cp`, Java **ignora** la variable `CLASSPATH`.
No se suman.

El valor por defecto es `.`, así que esto también funciona:

```bash
$ cd build/classes
$ javap GUI.VentanaTaquilla
Compiled from "VentanaTaquilla.java"
```

## Varias entradas: cuidado con el separador

| Sistema | Separador |
|---------|-----------|
| Linux / macOS | `:` |
| Windows | `;` |

```bash
# clases propias + un jar, en el mismo classpath
java -cp build/classes:dist/Taquilla.jar GUI.VentanaTaquilla
```

Usar el separador equivocado es un clásico. En Linux, `;` no separa: pasa a ser
**parte del nombre**, y Java busca un archivo llamado literalmente
`app.jar;dep.jar`:

```
$ java -cp "app.jar;dep.jar" app.App
Error: Could not find or load main class app.App
Caused by: java.lang.ClassNotFoundException: app.App
```

## `javac` también usa classpath

No es solo cosa de `java`. Al **compilar**, `javac` necesita encontrar las clases
que tus fuentes referencian. Compilar un único archivo que usa `Modelo` sin
decirle dónde está ese paquete falla:

```
$ javac -d /tmp/out src/GUI/VentanaTaquilla.java
src/GUI/VentanaTaquilla.java:3: error: package Modelo does not exist
import Modelo.Catalogo;
             ^
```

Con `-cp` apuntando a las clases ya compiladas, funciona:

```bash
$ javac -cp build/classes -d /tmp/out src/GUI/VentanaTaquilla.java
$ javac EXIT=0
```

La idea es la misma: **compilar también "busca"**. Solo cambia quién consume la
bandera:

| Consumidor | Bandera | Para qué la usa |
|------------|---------|-----------------|
| `javac` | `-cp` | Encontrar las clases que importas al compilar |
| `java` | `-cp` | Encontrar las clases que cargas al ejecutar |

## Las dos trampas que más tiempo hacen perder

### Trampa 1: `-jar` ignora `-cp`

`java -jar` toma **todo** del propio `.jar`. Las opciones de classpath como `-cp`
**no se aplican**. Lo probamos con dos jars (`app.jar` depende de `dep.jar`):

```
# app.jar NO incluye dep/Dep.class
$ java -jar app.jar
Exception in thread "main" java.lang.NoClassDefFoundError: dep/Dep

# aunque le pases la dependencia con -cp, la ignora:
$ java -cp dep.jar -jar app.jar
Exception in thread "main" java.lang.NoClassDefFoundError: dep/Dep

# la forma correcta: señalar tú la clase y ambas entradas
$ java -cp app.jar:dep.jar app.App
hola desde Dep
```

Para que `-jar` sí encuentre dependencias, se declaran **dentro del manifiesto**
con la entrada `Class-Path:`, y los jars deben quedar junto al principal. Es una
de las razones por las que empaquetar "bien" es más delicado que ejecutar con
`-cp`.

### Trampa 2: ejecutar desde la carpeta equivocada

Si tu única entrada es un classpath **relativo** y lanzas el comando desde otro
directorio, la ruta no existe y no encuentra nada. Soluciones: ejecutar siempre
desde la raíz del proyecto, usar rutas conocidas, o usar rutas absolutas.

## Qué significa cada error

| Error | Dónde ocurre | Qué pasó | Qué revisar |
|-------|--------------|----------|-------------|
| `package X does not exist` | Al **compilar** | `javac` no encontró el paquete | `-cp` de `javac` y ruta del paquete |
| `Could not find or load main class X` | Al **arrancar** | El lanzador no encontró la clase `main` | Nombre completo (con paquete) y `-cp` |
| `ClassNotFoundException` | Al **ejecutar** | La JVM buscó una clase y no estaba | Entrada del classpath que falta |
| `NoClassDefFoundError` | Al **ejecutar** | Compiló, pero una clase dependiente falta en runtime | Dependencia omitida (típico con `-jar`) |

## En este proyecto

`lotto-java` no tiene dependencias externas. Compilar y ejecutar:

```bash
javac -encoding UTF-8 -d build/classes $(find src -name '*.java')
java -cp build/classes GUI.VentanaTaquilla
```

`build/classes` es el **classpath** y `GUI.VentanaTaquilla` es la **clase**. El
`-d build/classes` del `javac` es justo lo que hace que luego esa carpeta sea un
classpath válido: allí se deposita la estructura `GUI/` y `Modelo/`.

Cuando un proyecto **sí** usa una librería (por ejemplo tu proyecto `Expendedora`,
que añade `Utilidades.jar`), NetBeans traduce eso a un classpath con **varias
entradas**. En `nbproject/project.properties` se ve así:

```
javac.classpath=\
    ${file.reference.Utilidades.jar}
```

y al ejecutar se convierte en algo como `-cp .../Utilidades.jar:build/classes`.

## Checklist

- [ ] ¿El nombre de la clase incluye el paquete? (`GUI.VentanaTaquilla`, no `VentanaTaquilla`)
- [ ] ¿La entrada del classpath es la **raíz** de los paquetes? (`build/classes`, no `build`)
- [ ] ¿Usaste el separador correcto? (`:` en Linux/macOS, `;` en Windows)
- [ ] Si usas `-jar`, ¿tienes claro que `-cp` se ignora?
- [ ] ¿Ejecutas desde la carpeta correcta si el classpath es relativo?
- [ ] Al compilar un solo archivo, ¿le pasaste `-cp` para resolver sus imports?

## Siguiente

Vuelve a [Ejecutar y depurar](08-ejecutar-y-depurar.md) para el resto de errores
comunes, o practica en [Ejercicios](09-ejercicios.md).
