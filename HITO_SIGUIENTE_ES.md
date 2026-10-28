# JeicyApotheoticAddition — siguiente hito de port (26/10/2026)

**Estado:** fuente de desarrollo; NO es mod compilado. **NO instalar en `mods`.**

## Cambios verificables hechos en este hito

- 22 recetas vanilla del Apotheotic Additions **V2.2.2 original** migradas a `data/apotheotic_additions/recipe/` para Minecraft 1.21.1. Antes había 2. Se preservaron los ingredientes, productos y cantidades; resultados ahora usan `id` cuando corresponde al formato actualizado. Las 22 son *candidatas de carga*, no confirmación de juego.
- Dos tags originales de ingredientes (`slate_reagents` y `slate_gems`) preservados en la ruta singular moderna `tags/item/` para evitar que `Gem-Bound Sigil` y el `Sigil of Socketing` pierdan ingredientes.
- 207 recetas restantes clasificadas por causa para revisión; no se copiaron ciegamente serializers antiguos (`apotheosis:fletching`, `apotheosis:salvaging`, malum, create, etc.) a la carpeta de runtime.
- Comprobación explícita de que **Heirloom Material / Core of the Family** y **Esoteric Material / Galactic Core** no se convierten en recetas básicas de crafting.
- Auditoría de categorías de los **263 affixes originales**: 57 tienen grupos de equipo con una posible equivalencia directa en el port 1.21; **203 requieren conservar reglas exactas mediante código/tag/predicate**, y 3 no definen el grupo. **No** se convierte `sword` en `apotheosis:melee_weapon` ni `pickaxe` en `apotheosis:breaker` (aceptarían ítems incorrectos).
- Los 263 affixes, 67 gemas, 229 recetas y las 67 reglas de gem_class siguen respaldados y comprobados contra el JAR original.

## Comprobaciones realizadas localmente

```text
python tools/test_original_parity.py /ruta/ApotheoticAdditionsV2.2.2.jar      OK
python tools/test_activated_vanilla_recipes.py /ruta/ApotheoticAdditionsV2.2.2.jar  OK
python tools/test_conversion.py                                               OK
python tools/check_runtime.py                                                 OK (validación JSON, NO carga del juego)
```

## Bloqueo para el JAR instalable

El entorno dispone de Java 21, pero **no Gradle ni las dependencias descargadas de NeoForge/Minecraft/Apotheosis**; las direcciones Maven no resuelven DNS en este entorno. No se ha compilado el proyecto con las APIs auténticas ni ejecutado Minecraft. La prueba de paridad de JSON NO equivale a que las recetas carguen realmente.

Compilación prevista con Gradle 8.8+ y acceso a red: `gradle build` (el proyecto puede mostrar errores por resolver). El JAR resultante no debe considerarse funcional hasta pasar una prueba de cliente y servidor.

## Próximo trabajo imprescindible

1. Obtener dependencias de desarrollo reales y corregir errores de compilación de registros NeoForge 21.1.251.
2. Portar y registrar codecs del formato Apotheosis 8.8 para gemas, rarezas, afijos, salvaging y reforging.
3. Desarrollar las equivalencias exactas de categorías, respetando `Sword`/`Axe`, `Pickaxe`/`Shovel`, etc.
4. Portar recetas de gemas con DataComponents (NBT 1.20.1 no se debe copiar sin adaptar).
5. Reproducción original de evolución de gemas, texturas y mesas sin requisitos del port AO.
6. Después, fase final: bosses, spawners, monedas, intercambios, drops e integraciones.

**Nombre final previsto:** `JeicyApotheoticAddition-1.21.1.jar`.
