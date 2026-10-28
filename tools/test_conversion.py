#!/usr/bin/env python3
"""Offline parity checks. Only validates staged drafts, NOT game compatibility."""
import json, pathlib, sys
root=pathlib.Path(__file__).resolve().parents[1]
st=root/'conversion_neoforge_1.21.1_NOT_RUNTIME'
expected={'affixes':263,'gems':67,'recipes':229,'rarities':4}
for key,n in expected.items():
 files=list((st/key).rglob('*.json'))
 assert len(files)==n, f'{key} {len(files)} != {n}'
 for p in files:
  obj=json.loads(p.read_text(encoding='utf8'));assert isinstance(obj,dict),p
r=json.loads((root/'AUDITORIA_CONVERSION_1_21_1.json').read_text())
assert r['affixes']['converted_draft']==263
assert r['gems']['converted_draft']==67
assert r['recipes']['converted_draft']==229
assert r['input_sha256']=='1d210294ee16e1a53b05b582ef7c61b6e5a6ff78dedad19d039b8fbb4b7c764a'
print('OK: 263 affixes, 67 gems, 229 recipes, 4 rarities. Drafts not runtime.')
