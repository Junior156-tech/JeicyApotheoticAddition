# Integración real de Gem Evolution en NeoForge 1.21.1

## Implementado y probado sin Minecraft
- Matriz **literal** de las 67 definiciones originales de gemas, todas sus bonificaciones y valores por rareza en `tier_engine_reference/original_gem_tier_values.json`.
- `OriginalGemTierLogic.java` verifica las nueve rarezas de la versión 1.20.1, transiciones teóricas y búsquedas de valores exactos, sin inventar escalados.

## NO implementado aún
- Registrar DataComponent para nivel original extendido y sincronizarlo entre servidor/cliente.
- Implementar Serializer real de `GemCuttingRecipe` para un upgrade solo si el requisito/coste coincide con el original (revisar fuente, no inventar recetas).
- Hook/mixin en `GemBonus` para consultar valores de la matriz original sin reinterpretarlos como purezas nativas de 1.21.
- Tooltips de gemas, salvaging y compatibilidad de `GemCase` y `GemCuttingTable`.
- Compatibilidad de registros de `apotheotic_additions:artifact/heirloom/esoteric` (NO mapeo engañoso a Perfect).

**Se han extraído los datos originales y probado la lógica base. No existe todavía mecánica de evolución funcional dentro del juego.**
