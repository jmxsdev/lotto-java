# Taquilla de Loterías — Java Swing

Proyecto **educativo** del **Módulo 1 de Java**: una aplicación de escritorio que
recrea la taquilla de una lotería, usada para practicar **POO**, el patrón
**MVC** y la programación de **aplicaciones de escritorio con Swing**.

> No es un port 1:1 de ninguna aplicación real: es el flujo de venta
> simplificado, con el dominio en memoria, pensado para aprender.

## Qué hace

- **Catálogo de juegos** (animalitos, terminales y tripletas) con sus horarios y
  multiplicador de premio.
- **Tablero de opciones** para elegir la apuesta, con resaltado de la selección.
- **Ticket en curso**: monto, premio potencial, total, cobrar y quitar jugadas.

## Requisitos

- **JDK 19 o superior** (desarrollado y probado con Java 27).

## Cómo ejecutar

**NetBeans:** abre el proyecto y pulsa **F6**.

**Terminal:**

```bash
javac -encoding UTF-8 -d build/classes $(find src -name '*.java')
java -cp build/classes GUI.VentanaTaquilla
```

## Manual

El manual didáctico completo está en [`doc/`](doc/README.md): POO aplicada, MVC,
fundamentos de Swing, recorrido del código línea a línea, el flujo de una venta,
`.class` vs `.jar`, el classpath, contratos e interfaces, y ejercicios.

## Estructura

```
src/Modelo/   dominio del negocio (no conoce Swing)
src/GUI/      vistas y controlador (Swing)
doc/          manual educativo en Markdown
```

## Estado

Proyecto en construcción con fines de aprendizaje.
