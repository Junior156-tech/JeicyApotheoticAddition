# JeicyApotheoticAddition — port fiel hacia Minecraft 1.21.1

**ESTADO: FUENTE DE DESARROLLO, NO MOD INSTALABLE** (26 de octubre de 2026).

Nombre visible solicitado: `JeicyApotheoticAddition`. **Identificador interno conservado:** `apotheotic_additions` para no romper los IDs originales de recetas y recursos. Eso significa que no se instala al mismo tiempo que `ao-apotheotic-port-*.jar` ni con el mod original 1.20.1.

## Referencia primaria

- `ApotheoticAdditionsV2.2.2.jar` subido por el usuario (Forge 1.20.1).
- SHA256: `1d210294ee16e1a53b05b582ef7c61b6e5a6ff78dedad19d039b8fbb4b7c764a`
- Objetivo de entorno: Minecraft `1.21.1`, NeoForge `21.1.251`, Apotheosis `8.8.0`, Java `21`.

## Trabajo conservado del original

- **263 affixes** en borradores, **67 gemas**, **229 recetas**, **3 rarezas**, **585 entradas de loot de affixes**, **67 sets de equipo**, **30 estadísticas de encantamiento** y assets originales.
- Java inicial de registros de ítems reales y lógica de niveles recuperada de los datos originales.
- Manifest `ORIGINAL_2_2_2_INVENTORY.json` con el hash SHA256 original de cada archivo de esas siete categorías.

## Cambios de esta iteración

1. Renombrado de proyecto, clase del mod, metadata visible y nombre del archivo de salida a `JeicyApotheoticAddition`.
2. Conservados el ID `apotheotic_additions` y los IDs de todos los contenidos originales, evitando una ruptura de recursos.
3. Añadida herramienta `tools/validar_jeicy.py` para validar los **67** JSON de gemas, **263** JSON de affixes y la integridad SHA de todas las categorías de referencia; no mezcla borradores de 1.20.1 con el runtime 1.21.1.
4. Añadidos `COMPILAR_SOLO_CON_DEPENDENCIAS_REALES.bat` y notas precisas: no generar un JAR con stubs ni falsificar compilación.

## Bloqueadores REALES — no están terminados

- Registros de bloques, menú/GUI y estanterías funcionales de NeoForge.
- Los codecs de 263 affixes y las restricciones de armas, validadas contra el Apotheosis real.
- Los 67 GemBonus y la integración de rarezas/pureza/evolución a través de las APIs de Apotheosis 8.8.0 (la clase pura Java no aplica cambios en Minecraft).
- Actualización de recetas NBT a componentes y serializers 1.21.1.
- Pruebas en un cliente/servidor con NeoForge/Apotheosis reales.
- Fase final postergada: bosses, minibosses, rogue spawners, monedas, intercambios, drops e integraciones.

## Cómo validar la fuente SIN instalar el mod

```powershell
py tools/validar_jeicy.py "C:\ruta\ApotheoticAdditionsV2.2.2.jar"
```

`gradle build` requiere **Gradle y conexión a los repositorios oficiales** para descargar NeoForge. Aquí no se pudo ejecutar porque la red de Maven/Gradle no está disponible. Incluso con compilación, los sistemas avanzados no estarían completos y no debe instalarse el JAR producido hasta terminar la adaptación.

## Importante para tu TEST

**No copies nada de este ZIP a `mods/` ni reemplaces tu modpack actual.** Es una fotografía de desarrollo para continuar el port fiel. No constituye una release funcional.
