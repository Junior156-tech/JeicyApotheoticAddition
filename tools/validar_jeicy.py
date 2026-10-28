#!/usr/bin/env python3
"""Compares the original 2.2.2 content and conversion workspace with an immutable inventory.
Nothing under migration_draft_NOT_RUNTIME is automatically loaded by Minecraft.
Run: py tools/validar_jeicy.py [path/to/ApotheoticAdditionsV2.2.2.jar]
"""
import hashlib,json,pathlib,sys,zipfile,collections
ROOT=pathlib.Path(__file__).resolve().parent.parent
inventory=json.loads((ROOT/'ORIGINAL_2_2_2_INVENTORY.json').read_text(encoding='utf-8'))
jar=pathlib.Path(sys.argv[1]) if len(sys.argv)>1 else None
problems=[]
if jar:
 if not jar.is_file(): problems.append(f'Original JAR no encontrado: {jar}')
 elif hashlib.sha256(jar.read_bytes()).hexdigest()!=inventory['reference_sha256']:
  problems.append('El SHA256 del JAR original no coincide')
 else:
  with zipfile.ZipFile(jar) as archive:
   for section,data in inventory['source_sections'].items():
    for item in data['items']:
     try: b=archive.read(item['original_path'])
     except KeyError: problems.append(f'Falta {item["original_path"]}'); continue
     if hashlib.sha256(b).hexdigest()!=item['sha256']: problems.append(f'Se alteró el original: {item["original_path"]}')
summary={}
for section,data in inventory['source_sections'].items():
 folder=ROOT/'migration_draft_NOT_RUNTIME/data/apotheotic_additions'/section
 drafts=list(folder.rglob('*.json'))
 original_count=data['count']
 summary[section]=(original_count,len(drafts))
 if len(drafts)!=original_count: problems.append(f'{section}: original={original_count} borradores={len(drafts)}')
 for f in drafts:
  try: json.loads(f.read_text(encoding='utf-8'))
  except Exception as exc: problems.append(f'JSON inválido: {f.relative_to(ROOT)}: {exc}')
# Prevent data-driven 1.20.1 files from being erroneously enabled as resources at runtime
runtime=ROOT/'src/main/resources/data/apotheotic_additions'
if runtime.exists():
 unsupported=['gems','affixes','rarities','recipes','gear_sets','enchanting_stats','affix_loot_entries']
 for section in unsupported:
  if (runtime/section).exists(): problems.append(f'ACTIVACIÓN NO REVISADA: {runtime/section} (no hay validación de codecs)')
print('JeicyApotheoticAddition — AUDITORÍA DEL PORT')
for k,(a,b) in summary.items(): print(f'  {k}: {a} originales / {b} borradores')
print('  JS/KubeJS AO no es una dependencia de este proyecto.')
print('  Los borradores fuera de src/main/resources no se activan en el juego.')
for issue in problems: print('ERROR:',issue)
if problems: sys.exit(1)
print('OK: integridad de datos y estructura (NO prueba del mod dentro de Minecraft)')
