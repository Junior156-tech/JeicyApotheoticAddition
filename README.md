# JeicyApotheoticAddition

Puerto **en desarrollo** de Apotheotic Additions 2.2.2 para Minecraft 1.21.1,
NeoForge y Apotheosis 8.8.0.

## Estado actual

- Gradle wrapper 8.12.1 y toolchain Java 21.
- Dependencias reales de Apotheosis 8.8.0 y sus módulos requeridos fijadas en
  `build.gradle`.
- Datos originales inventariados: 67 gemas, 263 affixes y 229 recetas.
- `compileJava` correcto contra NeoForge 21.1.251, Apotheosis 8.8.0, Placebo
  9.9.2 y Apothic Attributes 2.10.1.
- `clean test` correcto; cinco tests JUnit pasan sin fallos.
- El servidor de desarrollo carga nueve mods, 27 recetas activas y llega a
  `Done` sin errores de recetas o codecs.
- Apotheosis registra 36 gemas: sus 21 gemas base y 15 gemas originales
  activadas (`Armor`, `Hearty` y trece gemas crafted).
- Las 15 gemas activadas conservan pesos, calidad, unicidad, restricciones y
  valores exactos de los nueve rangos mediante el componente independiente
  `original_gem_rank`.
- El catálogo runtime ya conserva el rango mínimo original de las 67 gemas.
- Los bonuses portados cubren atributos simples, encantamientos globales y
  multiatributos con perfiles distintos para espada/tridente y arma pesada.
  Ur-Ghast conserva además su multiplicador simultáneo sobre 28 atributos.
- La mesa de corte conserva la evolución secuencial Common–Esoteric: consume
  dos gemas idénticas, `1 + rango×2` Gem Dust y material actual×3, siguiente×1
  o anterior×9. Las rutas nativa y extendida se verifican en cada arranque.
- Un validador restaura las categorías que Apotheosis 8.x fusionó; el arranque
  verifica 90 asignaciones de rango, diez decisiones de socket, cuatro perfiles
  multiatributo y los 28 atributos de Ur-Ghast.
- Las pruebas de paridad estática validan 67/67 gemas, 263/263 affixes,
  229/229 recetas y los 85 bonuses de atributos contra el JAR original.
- `apotheotic_additions:ancient_material` restaura el material eliminado por
  Apotheosis 8.8.0 y mantiene separados los costes Mythic y Ancient.

Quedan 52 gemas y los 263 affixes del port por activar en runtime. El JAR es un
artefacto de diagnóstico y **todavía no se declara funcional ni completo**. No
debe instalarse en mundos valiosos.

## Compilar

Se necesita JDK 21:

```powershell
.\gradlew.bat clean build
```

El artefacto objetivo es
`build/libs/JeicyApotheoticAddition-1.21.1.jar`.
