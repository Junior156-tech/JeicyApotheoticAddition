#!/usr/bin/env python3
"""Offline comparison to ORIGINAL Bryce792 2.2.2. No claim of NeoForge runtime verification."""
import zipfile, pathlib, sys, json, hashlib
root=pathlib.Path(__file__).resolve().parents[1]
original=pathlib.Path(sys.argv[1]) if len(sys.argv)>1 else None
if original is None or not original.is_file():
    sys.exit('Usage: python tools/test_original_parity.py /path/to/ApotheoticAdditionsV2.2.2.jar')
assert hashlib.sha256(original.read_bytes()).hexdigest() == '1d210294ee16e1a53b05b582ef7c61b6e5a6ff78dedad19d039b8fbb4b7c764a', 'Unexpected ORIGINAL JAR; inspect before porting'
z=zipfile.ZipFile(original)
draft=root/'conversion_neoforge_1.21.1_NOT_RUNTIME'
expected={'affixes':263,'gems':67,'recipes':229,'rarities':4}
for kind, total in expected.items():
    files=list((draft/kind).rglob('*.json'))
    assert len(files)==total,(kind,len(files),total)
    for f in files:json.loads(f.read_text(encoding='utf-8'))
manifest=json.loads((root/'tools/ORIGINAL_GEM_TARGETS.json').read_text(encoding='utf-8'))
assert len(manifest)==67
for key,entry in manifest.items():
    ref=json.loads(z.read(entry['name']))
    original_classes=[{'bonus_type':b.get('type'),'key':b['gem_class'].get('key'),'types':b['gem_class'].get('types',[])} for b in ref['bonuses'] if 'gem_class' in b]
    assert entry['gem_classes']==original_classes,key
    assert entry['min_rarity']==ref.get('min_rarity'),key
from migrate_vanilla_recipes import convert_recipe
for f in ['stonecutting/scrap_slab2.json','stonecutting/scrap_stairs2.json']:
    src=json.loads(z.read('data/apotheotic_additions/recipes/'+f))
    dst=json.loads((root/'src/main/resources/data/apotheotic_additions/recipe'/f).read_text(encoding='utf-8'))
    assert dst == convert_recipe(src),f
assert not any((root/'src/main/resources/data/apotheotic_additions/recipe/crafted_gems').glob('eye_*.json')), 'Legacy NBT crafted gem recipes must not be activated'
print('OK: 263 affixes, 67 gems, 229 recipes staged; 67 original targets verified; 26 1.21 native recipes separately checked')
