# JeicyApotheoticAddition

Puerto **en desarrollo** de Apotheotic Additions 2.2.2 para Minecraft 1.21.1,
NeoForge y Apotheosis 8.8.0.

## Estado actual

- Gradle wrapper 8.12.1 generado.
- Toolchain objetivo: Java 21.
- Dependencias reales de Apotheosis 8.8.0 y sus módulos requeridos fijadas en
  `build.gradle`.
- Datos originales inventariados: 67 gemas, 263 affixes y 229 recetas.
- Primera compilación real ejecutada el 28 de octubre de 2026.
- `compileJava`: **correcto** contra los binarios reales de NeoForge 21.1.251,
  Apotheosis 8.8.0, Placebo 9.9.2 y Apothic Attributes 2.10.1.
- `clean build`: **correcto**; cuatro tests JUnit pasan sin fallos.
- Servidor dedicado de desarrollo: carga los nueve mods, las 26 recetas
  activadas y llega a `Done` sin errores de recetas.
- Las pruebas de paridad estática validan 67/67 gemas, 263/263 affixes,
  229/229 recetas y los 85 bonuses de atributos contra el JAR original.
- `apotheotic_additions:ancient_material` restaura el material eliminado por
  Apotheosis 8.8.0; conserva separado el coste Mythic/Ancient de las recetas.

El servidor solo registra por ahora los 21 gems y 94 affixes base de
Apotheosis. Las 67 gemas y 263 affixes del port siguen en directorios
`NOT_RUNTIME` mientras se portan sus codecs, clases de equipo y efectos. El JAR
se puede usar como artefacto de diagnóstico, pero **todavía no se declara
funcional ni completo** y no debe instalarse en mundos valiosos.

## Compilar

Se necesita JDK 21:

```powershell
.\gradlew.bat clean build
```

El artefacto objetivo, cuando las verificaciones pasen, será
`build/libs/JeicyApotheoticAddition-1.21.1.jar`.

