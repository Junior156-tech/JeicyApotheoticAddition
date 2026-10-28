# Hito del port (solo uso interno; NO instalable)

Se incorporó en `src/main/java` el primer componente real de datos del mod
NeoForge 1.21.1: `apotheotic_additions:original_gem_rank` (índices 0 a 8),
registrado con `DeferredRegister.DataComponents`, serialización persistente y
sincronización de red. `JeicyGemRankAccess` lo asigna solamente a `GemItem` de
Apotheosis y mantiene la pureza nativa separada. El enum `OriginalGemRank`
conserva exactamente la progresión original Common→Esoteric, sin renombrar
`Perfect` ni aceptar saltos de nivel.

La definición del componente y el acceso a ItemStack se contrastaron con la
API pública de NeoForge 1.21.1 y el código público de `GemItem` de Apotheosis;
**NO están compilados aún contra los JARs reales.** Sí pasó `javac` la lógica
Java independiente de los nueve niveles.

Recetas vanilla originales migradas y comparadas: 26 (antes 22), incluyendo
las cuatro estanterías. **Estas estanterías todavía usan clases Block básicas;
no replican aún sus mecánicas de encantamiento originales.**

**FALTA, Y ES BLOQUEANTE:** producir las 67 definiciones compatibles con la API
8.8.0, sus 193 bonos activos y los 263 affixes, conectar la lectura del nuevo
componente con los cálculos de gemas y la mesa de evolución, adaptar la lógica
especial de los shelves, migrar las recetas dependientes de compatibilidad,
compilar con dependencias reales y probar en cliente/servidor. No publicar JAR
ni activar datos incompatibles hasta que ocurra.
