# JeicyApotheoticAddition — continuación real del port 1.21.1

**Entrega:** fuente de desarrollo; NO es un mod instalable ni un JAR compilado contra NeoForge.

## Cambios sustantivos

- 263 affixes convertidos a **borradores de esquema 1.21.1**, conservando sus valores numéricos y agrupaciones originales por rareza. Incluye informe detallado de codecs, objetivos exactos y condiciones por mod.
- 67 gemas convertidas en borradores comparables a 1.21.1, conservando bonuses originales; NO se asumió que rareza original equivale a pureza moderna.
- 229 recetas clasificadas y parcialmente migradas a formato de 1.21.1 (condiciones `neoforge:mod_loaded`, resultado `id` para crafting, etc.), sin activar recetas que dependen de otros mods.
- Cuatro rarezas de equipo (Ancient, Artifact, Heirloom, Esoteric) preparadas como borradores con colores y reglas del original.
- Clase pura Java `OriginalSocketRules` más pruebas para no permitir la compatibilidad incorrecta de espada/hacha.
- **21 registros de bloques y 21 BlockItems** añadidos al proyecto NeoForge con subtipos correctos para slab, stairs, glass/pane y pillar; pendiente portar los efectos originales de shelves y las propiedades exactas. Registros no compilados contra dependencias reales.
- Auditoría generada en `AUDITORIA_CONVERSION_1_21_1.json`.

## Restricciones importantes

No se han activado estos JSON en `src/main/resources/data` porque contienen campos `_migration_notes` o `__unported_...`, y necesitan validarse contra los codecs de **Apotheosis 8.8.0**. `conversion_neoforge_1.21.1_NOT_RUNTIME/` es intencionalmente una carpeta fuera del runtime.

Falta implementar registros de clases de arma/gema equivalentes, serializers de bonus no estándar, estadísticas de shelves, recetas y evolución de gemas en el juego; además de compilar con NeoForge/Apotheosis reales. El `.jar` anterior NO era apto para jugar. No está resuelto el port completo.

La revisión del mod original 2.2.2 tiene prioridad sobre el port AO de terceros. No se inventaron recetas nuevas ni se convirtió `Core of the Family` o `Galactic Core` en materiales crafteables fáciles.

## Validación

```bash
python tools/test_conversion.py
python tools/validar_jeicy.py /ruta/ApotheoticAdditionsV2.2.2.jar
```

Para validar el puro Java sobre las restricciones originales:
```bash
mkdir -p build/plain-java
javac -d build/plain-java src/main/java/org/jeicy/apotheoticadditions/OriginalSocketRules.java pure_java_test/TestOriginalSocketRules.java
java -ea -cp build/plain-java TestOriginalSocketRules
```

**No copies nada de este ZIP a `mods` ni a `kubejs`.**
