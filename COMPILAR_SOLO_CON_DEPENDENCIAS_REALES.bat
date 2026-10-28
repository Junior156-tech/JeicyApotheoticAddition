@echo off
chcp 65001 >nul
echo JeicyApotheoticAddition - compilacion de desarrollo
echo AVISO: incluso si compila, NO esta listo para instalar en Minecraft.
where gradle >nul 2>nul
if errorlevel 1 (
  echo No se encontro Gradle. Instala Gradle 8.10 o superior y Java 21.
  pause
  exit /b 1
)
gradle --no-daemon build
if errorlevel 1 (
 echo Error en compilacion. No se creo mod funcional.
 pause
 exit /b 1
)
echo Artefacto de desarrollo: build\libs\JeicyApotheoticAddition-*.jar
echo NO INSTALAR hasta portar y validar gemas, affixes, rarezas y recetas.
pause
