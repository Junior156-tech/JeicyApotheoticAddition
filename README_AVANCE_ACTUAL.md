# JeicyApotheoticAddition — siguiente hito de desarrollo (Minecraft 1.21.1)

**Importante:** NO es aún un JAR funcional. No colocar ZIP ni previews en `mods`.

## Cambios realmente realizados

1. Configuración de compilación ajustada para **NeoForge 21.1.251**, Java 21 y artefacto **Apotheosis 8.8.0** desde Modrinth Maven. No requiere AO Port ni KubeJS. La configuración NO ha podido ejecutarse aquí: este contenedor carece de Gradle, de la caché de dependencias y de salida a Maven.
2. Activadas en `src/main/resources/data/apotheotic_additions/recipe/` dos recetas vanilla de stonecutting migradas del original: `scrap_slab2` y `scrap_stairs2`. Se conservaron exactamente ingrediente, resultado y cantidad; son candidatas de runtime solo cuando el JAR compile y registre los bloques. No dependen de sistemas especiales.
3. Creado inventario completo `tools/ORIGINAL_GEM_TARGETS.json` con el targeting de las 67 gemas originales, atributo por atributo; evita perder Sword vs Axe/Heavy Weapon y otras restricciones por mapas genéricos.
4. Añadidos tests de paridad exacta con el JAR original por SHA256, los 263 affixes, 67 gemas, 229 recetas, las 67 configuraciones de targeting y la integridad de las 2 recetas cargables.
5. Conservados los 21 registros de bloques, 13 ítems base y las conversiones anteriores en `conversion_neoforge_1.21.1_NOT_RUNTIME`. **No se han presentado como features jugables.**

## No completado

- La compilación real y arranque en NeoForge 1.21.1.
- Registros correctos de affixes/gemas/rareza y evolución Java para Apotheosis 8.8.0.
- Conversión de recetas de gemas con NBT 1.20.1 a Data Components 1.21.1, y sus serializers.
- Comportamiento real de enchanting shelves y todos los efectos propios.
- 263 affixes y 67 gemas NO están activados en runtime hasta validar sus serializers, categorías y valores.
- Bosses, minibosses, rogue spawners, monedas, intercambios, drops e integraciones especiales siguen pospuestos.

## Pruebas de esta entrega

```
python tools/test_conversion.py
python tools/test_original_parity.py /ruta/ApotheoticAdditionsV2.2.2.jar
python tools/check_runtime.py
```

Con Gradle 8.10+ y dependencias en línea, un desarrollador debe ejecutar `gradle build` para obtener errores de compilación concretos (no garantizamos que compile sin correcciones).

La meta final sigue siendo **un único `JeicyApotheoticAddition-1.21.1.jar` funcional**. No se debe instalar ningún preview hasta realizar un build real y probarlo en cliente/servidor TEST.
