# ESTADO DE AVANCE — JeicyApotheoticAddition 1.21.1

## Trabajo realizado en esta continuación

- 263 affixes originales: convertidos **solo a borradores** con adaptación de estructura NeoForge y preservación de valores.
- 67 gemas originales: convertidas **solo a borradores**. No se ha alterado ni simulado su rareza/pureza.
- 229 recetas originales: clasificadas y reescritas parcialmente, pero sin activación automática.
- 4 rarezas (incluida Ancient): borradores con colores y reglas tomados del mod original.
- 21 bloques originales: registros NeoForge añadidos en Java, además de sus BlockItems (base sin efectos completos).
- Restricciones exactas de clases de arma: código Java de referencia y prueba ejecutada.

## Comprobaciones ejecutadas

- Python: JSON de 263 affixes, 67 gemas, 229 recetas y 4 rarezas válido.
- Python: integridad SHA256 de todos los ficheros del JAR 2.2.2 verificada.
- Java: `OriginalSocketRules` compiló con `javac` real y pasó sus aserciones.
- Java NeoForge: **NO COMPILADO**, falta Gradle y las dependencias NeoForge y Apotheosis (sin acceso a sus repositorios Maven en este entorno).
- Dentro de Minecraft: **NO PROBADO**.

## Falta para JAR jugable

1. Registrar correctamente APIs de Apotheosis 8.8.0 para rarezas, afijos y gem bonuses.
2. Resolver categorías exactas de arma/Curios en 166 afijos, más 9 tipos especiales.
3. Implementar las 67 gemas en runtime y la evolución fiel, incluyendo DataComponents, menús y salvaging.
4. Completar stats de los shelves y otras mecánicas de 21 bloques; recetas/tagging.
5. Compilar con NeoForge 21.1.251 / Minecraft 1.21.1 / Apotheosis 8.8.0 y probar en TEST.
6. Fase posterior: bosses, minibosses, rogue spawners, monedas, intercambios, drops e integraciones.

**No instales este ZIP ni el JAR preliminar anterior en la carpeta `mods`.** No existe todavía un `.jar` instalable y verificado. 
