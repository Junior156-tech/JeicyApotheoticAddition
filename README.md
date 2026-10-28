# JeicyApotheoticAddition

Puerto **en desarrollo** de Apotheotic Additions 2.2.2 para Minecraft 1.21.1,
NeoForge y Apotheosis 8.8.0.

## Estado del checkpoint inicial

- Gradle wrapper 8.12.1 generado.
- Toolchain objetivo: Java 21.
- Dependencias reales de Apotheosis 8.8.0 y sus módulos requeridos fijadas en
  `build.gradle`.
- Datos originales inventariados: 67 gemas, 263 affixes y 229 recetas.
- Primera compilación real ejecutada el 28 de octubre de 2026.
- Compilación actual: **fallida** por la migración pendiente de `GlassBlock` en
  `JeicyOriginalBlocks.java`.

Este checkpoint no contiene un JAR funcional. Los directorios con sufijo
`NOT_RUNTIME` son material de conversión y auditoría; no se cargan como
datapack del mod.

## Compilar

Se necesita JDK 21:

```powershell
.\gradlew.bat clean build
```

El artefacto objetivo, cuando las verificaciones pasen, será
`build/libs/JeicyApotheoticAddition-1.21.1.jar`.

