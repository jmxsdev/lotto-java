# Manual educativo — Taquilla de Loterías

Este manual explica, paso a paso, cómo está construida la aplicación de
escritorio **Taquilla de Loterías** en Java Swing. Está pensado para personas
con un nivel intermedio de Java que quieren ver un proyecto completo: desde las
clases del dominio hasta la interfaz gráfica, pasando por el flujo real de una
venta.

No es una guía de "copia y pega". Cada decisión se justifica con el código real
del proyecto y los ejemplos se citan directamente de los archivos en `src/`.

## Qué aprenderás

| Archivo | Qué aprendes | Cuándo leerlo |
|---------|--------------|---------------|
| [01-el-proyecto.md](01-el-proyecto.md) | Contexto, alcance y flujo de negocio | Primero, para entender qué se construyó |
| [02-poo-en-java.md](02-poo-en-java.md) | POO aplicada al dominio del proyecto | Antes de tocar el Modelo |
| [03-arquitectura-mvc.md](03-arquitectura-mvc.md) | Separación Modelo / Vista / Controlador | Cuando quieras entender por qué está dividido así |
| [04-swing-fundamentos.md](04-swing-fundamentos.md) | Componentes, layouts, eventos y EDT | Antes de tocar la GUI |
| [05-modelo-linea-a-linea.md](05-modelo-linea-a-linea.md) | Cada clase del Modelo, campo y método | Al leer o modificar `src/Modelo` |
| [06-gui-linea-a-linea.md](06-gui-linea-a-linea.md) | Cada clase de la GUI, su rol y partes clave | Al leer o modificar `src/GUI` |
| [07-flujo-de-una-venta.md](07-flujo-de-una-venta.md) | El click completo, método por método | Para entender la interacción en vivo |
| [08-ejecutar-y-depurar.md](08-ejecutar-y-depurar.md) | Compilar, ejecutar y depurar | Antes de tu primera ejecución |
| [09-ejercicios.md](09-ejercicios.md) | Práctica guiada en tres niveles | Cuando ya entiendas el código |
| [10-glosario.md](10-glosario.md) | Definiciones rápidas de términos | Como referencia en cualquier momento |
| [11-classpath.md](11-classpath.md) | Dónde busca Java tus clases (`-cp`) | Al ejecutar o al leer errores de "class not found" |
| [12-contratos-e-interfaces.md](12-contratos-e-interfaces.md) | Contratos: interfaz vs clase abstracta | Cuando confundas ambos conceptos |

## Dos rutas de lectura

**Si eres nuevo en Java:** recorre el manual en orden. Detente en cada archivo,
compila el proyecto y ejecútalo, y solo entonces pasa al siguiente:

```
01 → 02 → 03 → 04 → 05 → 06 → 07 → 08 → 09 → 10
```

**Si ya sabes Java:** ve directo a [Arquitectura MVC](03-arquitectura-mvc.md) y
luego a los recorridos de código [Modelo](05-modelo-linea-a-linea.md) y
[GUI](06-gui-linea-a-linea.md). Usa [Flujo de una venta](07-flujo-de-una-venta.md)
para la interacción y el [Glosario](10-glosario.md) como referencia. Deja
[POO](02-poo-en-java.md) y [Swing](04-swing-fundamentos.md) para consulta puntual.

## Cómo ejecutar el proyecto

Las instrucciones completas (NetBeans, terminal y `.jar`) están en
[Ejecutar y depurar](08-ejecutar-y-depurar.md). El resumen es:

```bash
javac -encoding UTF-8 -d build/classes $(find src -name '*.java')
java -cp build/classes GUI.VentanaTaquilla
```

Para empaquetar todo en un solo `dist/Taquilla.jar` y ejecutarlo con
`java -jar`, mira
[Clases `.class` y archivos `.jar`](08-ejecutar-y-depurar.md#clases-class-y-archivos-jar).
Si un comando te da `class not found`, o dudas de qué es `-cp`, mira
[El classpath](11-classpath.md).

## Al terminar este manual podrás...

- [ ] Explicar la diferencia entre Modelo, Vista y Controlador usando este proyecto.
- [ ] Leer cualquier clase de `src/Modelo` y describir qué garantiza.
- [ ] Justificar por qué el Modelo no importa `javax.swing`.
- [ ] Seguir el recorrido de un click desde `PanelTablero` hasta `Ticket`.
- [ ] Agregar un juego nuevo al `Catalogo` sin romper el resto.
- [ ] Compilar y ejecutar la aplicación desde la terminal.
- [ ] Explicar qué es el classpath y por qué `java -jar` ignora `-cp`.
- [ ] Depurar un problema de interfaz con `System.out.println` o breakpoints.

## Siguiente

Continúa con [El proyecto](01-el-proyecto.md) para entender de dónde viene la
aplicación y cuál es su alcance.
