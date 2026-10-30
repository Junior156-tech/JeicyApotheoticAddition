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
- El servidor de desarrollo carga las dependencias reales, mantiene 27 recetas
  activas del puerto y llega a `Done` sin errores de recetas o codecs.
- Apotheosis registra 53 gemas en el entorno completo y el verificador confirma
  30 gemas originales activas con sus nueve rangos.
- El JAR incluye 31 definiciones runtime; Sunbird sigue siendo condicional
  porque requiere Mowzie's Mobs.
- La gema Aether vuelve a habilitar vuelo con élitros en casco y vuelo creativo
  en pechera; el entorno de desarrollo fija Aether 1.21.1 y owo-lib reales.
- Purity y Zanite Alchemist conservan transformaciones de drops, atributos,
  efectos, cooldowns y restricciones exactas sobre pico, armadura y armas.
- Naga transforma troncos en Liveroot exclusivamente con armas pesadas; el
  entorno de desarrollo fija Twilight Forest 4.8.3802 desde su Maven oficial.
- Decay y Undergarden conservan alquimia, salud, robo de vida, velocidad de
  minería, Armor Shred y Virulence; se prueba The Undergarden 0.9.6 real.
- Heartland conserva sus dos transformaciones de bloques, Backstabbing y
  Comfort; se prueba Farmer's Delight 1.3.4 real.
- Harbringer, Leviathan y Remnant se cargan y verifican contra L_Ender's
  Cataclysm 3.33 real, junto con Lionfish API 3.1 y Curios API 9.5.1.
- La gema general de Cataclysm conserva Monstrous, Blazing Brand y Abyssal
  Curse, incluida su acumulación al reaplicar y la separación hacha/espada.
- Archmage, Ars Mana y Bee Queen conservan atributos, encantamiento, efectos,
  acumulación y sockets originales con Ars Nouveau y Bumblezone reales.
- Las gemas activadas conservan pesos, calidad, unicidad, restricciones y
  valores exactos de los nueve rangos mediante el componente independiente
  `original_gem_rank`.
- El catálogo runtime ya conserva el rango mínimo original de las 67 gemas.
- Los bonuses portados cubren atributos simples, encantamientos globales y
  multiatributos con perfiles distintos para espada/tridente y arma pesada.
  Ur-Ghast conserva además su multiplicador simultáneo sobre 28 atributos.
- Midas mantiene separados el casco Mageslayer, Sweeping para espada/tridente
  y la transformación de retoños en manzanas doradas para armas pesadas.
- El bonus original de efectos temporales ya usa rangos Common–Esoteric y
  respeta duración, amplificador, cooldown, objetivo y tipo de socket.
- La mesa de corte conserva la evolución secuencial Common–Esoteric: consume
  dos gemas idénticas, `1 + rango×2` Gem Dust y material actual×3, siguiente×1
  o anterior×9. Las rutas nativa y extendida se verifican en cada arranque.
- Un validador restaura las categorías que Apotheosis 8.x fusionó; el arranque
  verifica 180 asignaciones de rango, decisiones exactas de socket, cuatro
  perfiles multiatributo y los 28 atributos de Ur-Ghast.
- Las pruebas de paridad estática validan 67/67 gemas, 263/263 affixes,
  229/229 recetas y los 85 bonuses de atributos contra el JAR original.
- `apotheotic_additions:ancient_material` restaura el material eliminado por
  Apotheosis 8.8.0 y mantiene separados los costes Mythic y Ancient.

Quedan 36 definiciones de gemas por portar y los 263 affixes por activar. En la
configuración de desarrollo se registran 30 gemas; la definición de Sunbird se
omite porque Mowzie's Mobs no está instalado. El JAR es un
artefacto de diagnóstico y **todavía no se declara funcional ni completo**. No
debe instalarse en mundos valiosos.

## Compilar

Se necesita JDK 21:

```powershell
.\gradlew.bat clean build
```

El artefacto objetivo es
`build/libs/JeicyApotheoticAddition-1.21.1.jar`.
