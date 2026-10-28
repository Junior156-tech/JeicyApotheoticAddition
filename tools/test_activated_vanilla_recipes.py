#!/usr/bin/env python3
"""Verifies all activated native recipes against ORIGINAL 2.2.2.
Static verification only: MUST NOT be presented as game/runtime compatibility.
"""
import json
import pathlib
import sys
import zipfile
from migrate_vanilla_recipes import convert_recipe
root=pathlib.Path(__file__).resolve().parents[1]
original=pathlib.Path(sys.argv[1]) if len(sys.argv)>1 else None
if not original or not original.exists(): sys.exit('Pass path to original 2.2.2 JAR')
activated=list((root/'src/main/resources/data/apotheotic_additions/recipe').rglob('*.json'))
assert len(activated)==26, f'Expected 26 native recipes; found {len(activated)}'
with zipfile.ZipFile(original) as z:
 for p in activated:
  rel=p.relative_to(root/'src/main/resources/data/apotheotic_additions/recipe')
  raw=json.loads(z.read('data/apotheotic_additions/recipes/'+str(rel).replace('\\','/')))
  candidate=json.loads(p.read_text())
  assert candidate==convert_recipe(raw), str(rel)
  assert candidate['type'].startswith('minecraft:'), str(rel)
  assert candidate['result']['id'] not in ('apotheotic_additions:heirloom_material','apotheotic_additions:esoteric_material')
 for name in ['slate_reagents','slate_gems']:
  former=json.loads(z.read(f'data/apotheotic_additions/tags/items/{name}.json'))
  current=json.loads((root/f'src/main/resources/data/apotheotic_additions/tags/item/{name}.json').read_text())
  assert former==current, name
print('OK: 26 native recipes and 2 ingredient tags structurally match the original; NOT TESTED IN MINECRAFT')
