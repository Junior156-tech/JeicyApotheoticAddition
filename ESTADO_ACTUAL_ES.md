# Estado actual — TEST.22

Puerto en desarrollo para Minecraft 1.21.1, NeoForge 21.1.251 y Apotheosis
8.8.0. No usa AO Port ni KubeJS.

## Activo y verificado en servidor

- 55 JSON de gemas empaquetados; 54 se activan en el entorno completo actual.
- 324 asignaciones de los nueve rangos originales verificadas en cada arranque.
- Ancient, Artifact, Heirloom y Esoteric activas junto a las cinco rarezas de
  Apotheosis 8, para un total de nueve.
- Codec de rareza propio que reproduce probabilidades, fallbacks, sockets
  incrementales y los 15 pasos discretos de durabilidad de 1.20.1.
- 8 affixes de estadísticas de armadura Artifact/Heirloom/Esoteric activos y
  comparados con los valores originales.
- 27 recetas runtime y evolución secuencial de gemas Common–Esoteric.

El servidor de desarrollo registra 9 rarezas, 105 affixes y 77 gemas, llega a
`Done` y ejecuta los verificadores de gemas, rarezas y affixes. Los errores de
recetas de compatibilidad de Malum/Rats y los avisos de Alex's Mobs proceden de
esas dependencias externas y también aparecen sin los datos nuevos del port.

## Pendiente

- 12 definiciones de gemas cuyos mods fuente no tienen una versión oficial
  comprobada para 1.21.1.
- 255 affixes, 202 recetas y el resto de sistemas de loot, bloques y contenido.
- Restaurar la animación arcoíris de Ancient; por ahora usa un púrpura fijo.
- Pruebas de cliente y pruebas manuales prolongadas en juego.

El JAR sigue siendo un artefacto de TEST. No debe usarse en mundos valiosos ni
se declara completo o listo para producción.
