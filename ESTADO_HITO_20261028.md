# JeicyApotheoticAddition 1.21.1 — Hito de integración del 28/10/2026

ARCHIVO DE TRABAJO, NO JUGABLE, NO INSTALAR EN mods.

Se agregó `OriginalRankValues.java` (Java puro) para valores EXACTOS de los nueve niveles de gemas. Rechaza IDs inválidos, cantidades no finitas y evita extrapolar escalones no definidos.

Se agregó `JeicyTieredAttributeBonus.java`, implementación de `GemBonus` inspirada en la API de Apotheosis rama 1.21, que usa `GemView.gemStack()` y `original_gem_rank` para escoger las bonificaciones del rango original, no la pureza nativa `Perfect`.

Se registró un tipo de bonus `apotheotic_additions:original_attribute` en la clase principal. ESTO TODAVÍA NO SE HA COMPILADO CONTRA LOS BINARIOS REALES de Apotheosis 8.8.0; verificar nombres y firmas antes de usar.

Se generaron 85 bonuses de atributos originales con sus valores, clase de arma, operación e IDs, guardados bajo `conversion_neoforge_1.21.1_NOT_RUNTIME/ranked_attribute_bonuses/`. No están activos en los datapacks porque requieren correspondencia exacta de `gem_class` original al registro `LootCategory` de Apotheosis 8.8.0. Nunca convertir `Sword` en `melee_weapon` indiscriminadamente.

Pruebas que pasaron en entorno sin dependencias NeoForge:
- javac/java: TestOriginalGemRank, TestOriginalRankValues, TestGemTierLogic, TestOriginalSocketRules.
- Python: 85 atributos comparados campo por campo con JAR 2.2.2.
- Python: paridad de 263 affixes, 67 gems, 229 recipes.
- Python: 26 recetas vanilla 1.21.1 comparadas con origen.

Pendiente ANTES de JAR jugable: compilar las clases integradas contra NeoForge 21.1.251/Apotheosis 8.8.0; portar GemClass exactas; conectar 85 bonuses a definiciones runtime, y luego 108 bonuses de los demás 8 tipos, rarezas, recipes, mesetas, evolución y GUI. Aquí no existe Gradle/cache ni acceso DNS a Maven externo. No prometer un JAR viable.
