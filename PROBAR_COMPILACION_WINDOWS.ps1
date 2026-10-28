# Development compilation check only; NOT a game installer.
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
if (-not (Get-Command java -ErrorAction SilentlyContinue)) { throw 'Instala Java 21 primero.' }
$javaVersion = (& java -version 2>&1 | Out-String)
if ($javaVersion -notmatch 'version "21\.') { throw "Se requiere Java 21. Actual: $javaVersion" }
if (-not (Get-Command gradle -ErrorAction SilentlyContinue)) { throw 'Falta Gradle 8.8+ en el PATH (el ZIP NO incluye wrapper). Instala Gradle o abre el proyecto con IntelliJ y configuralo.' }
Write-Host 'Iniciando compilación de desarrollo. Se necesita internet para dependencias reales...'
& gradle --no-daemon build *>&1 | Tee-Object -FilePath 'BUILD_LOG.txt'
if ($LASTEXITCODE -ne 0) { throw 'El build falló. BUILD_LOG.txt contiene errores concretos que debemos corregir. No instales nada.' }
Write-Host 'Build terminó: revisión de compilación, NO certificación de gameplay.'
Write-Host 'Antes de instalar un JAR, deben comprobarse affixes, gemas, recetas y servidor.'
