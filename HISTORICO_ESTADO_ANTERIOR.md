# Apotheotic Additions 1.21.1 — Fases 1 a 5: FUENTE DE MIGRACIÓN, no JAR jugable

**Referencia primaria:** `ApotheoticAdditionsV2.2.2.jar`; SHA256 `1d210294ee16e1a53b05b582ef7c61b6e5a6ff78dedad19d039b8fbb4b7c764a`.

## Contenido entregado

- 263 affixes del original copiados y clasificados, con 263 borradores transformados para el formato 1.21.1 (`migration_draft_NOT_RUNTIME/data/apotheotic_additions/affixes`). Se conservaron valores, familias, efectos y condiciones originales; pero la corrección de los codecs requiere revisión y pruebas con Apotheosis real. Algunas categorías nuevas son más amplias que las originales, especialmente Sword vs Axe. **No declaramos que funcionen los 263 aún.**
- 67 gemas originales conservadas, con 67 borradores 1.21.1; las matrices originales de todas sus bonificaciones y rarezas están en `tier_engine_reference/original_gem_tier_values.json`.
- 229 recetas originales, 3 rarezas adicionales, 585 afijos de loot, 67 gear sets, 30 enchanting stats. Copias completas dentro de `original_1.20.1_REFERENCE`. No se colocaron sin traducir en runtime.
- Texturas, nombres y modelos originales preservados en `src/main/resources/assets`. Código Java inicial para ítems básicos y lógica Java pura de nueve rarezas originales.
- `OK: 9 original tiers, 8 transitions, no post-Esoteric, ID isolation, exact bonuses`.

## IMPORTANTE — ETAPA REAL

Este **NO** es un port ejecutable de fases 1–5 ni incluye evolución de gemas en el juego. La conversión no se puede validar aquí contra Minecraft/NeoForge/Apotheosis: no hay artefactos de desarrollo instalados y la red para Gradle está bloqueada. La solución correcta es compilar contra dependencias reales y después confirmar comportamientos en un Minecraft 1.21.1 aislado.

## Por qué no se instala tal cual
- El código Java de los bloques/enchants/rarities y los hooks para tier de gemas todavía necesitan ser portados.
- Las recetas originales usan NBT y otras APIs de Forge 1.20.1.
- Algunas recetas y affixes dependen de otros mods; su integración queda para última fase.
- No inventamos un nuevo sistema de crafteo para acelerar el proyecto.
- `src/main/resources/data` no contiene las recetas antiguas porque provocarían fallos; todos los borradores están **fuera** de recursos runtime.

## Qué validar para construir un JAR completo
1. Configurar Gradle/NeoForge 21.1.251 y Apotheosis 8.8.0 reales.
2. Portar y probar los tres registros de rareza y el Ancient proporcionado por Apotheosis original o adaptado.
3. Resolver categorías exclusivas (Sword solo Sword, Axe solo Axe, etc.) antes de activar los affixes.
4. Portar 263 affixes validando Codec y dependencias de mods.
5. Portar 67 gems verificando GemClass, bonuses, condiciones y purezas.
6. Implementar DataComponents y GemCuttingRecipe real para evolución de gemas; probar cliente/servidor y salvaging.
7. Portar todos los bloques, enchanting stats, recetas y modelos funcionales.
8. Dejar bosses/minibosses, rogue spawners, monedas/intercambios/drops, integraciones últimas.

El archivo ZIP es código fuente y materiales de migración, **no un instalador ni una build de servidor**.
