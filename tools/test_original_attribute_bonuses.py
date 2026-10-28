#!/usr/bin/env python3
"""Full-value parity for the 85 tiered attribute bonuses of the original mod."""
import json
from pathlib import Path
from zipfile import ZipFile
import sys

jar = Path(sys.argv[1])
root = Path(sys.argv[2])
base = root / 'conversion_neoforge_1.21.1_NOT_RUNTIME/ranked_attribute_bonuses'
assert base.exists()
count = 0
with ZipFile(jar) as z:
    for filename in sorted(f for f in z.namelist() if f.startswith('data/apotheotic_additions/gems/') and f.endswith('.json')):
        orig = json.loads(z.read(filename))
        targets = [(i, b) for i, b in enumerate(orig.get('bonuses', [])) if b.get('type') == 'apotheosis:attribute']
        path = base / filename.split('data/apotheotic_additions/gems/', 1)[1]
        if not targets:
            assert not path.exists(), str(path)
            continue
        exported = json.loads(path.read_text())['attribute_bonuses']
        assert len(exported) == len(targets)
        for data, (i, original) in zip(exported, targets):
            count += 1
            assert data['values'] == original['values'], (filename,i,'values')
            assert data['attribute'] == original['attribute'], (filename,i,'attribute')
            assert data['__requires_exact_gem_class'] == original['gem_class'], (filename,i,'class')
            assert data['__origin'] == f'{filename.split("data/apotheotic_additions/gems/",1)[1]}#bonuses[{i}]'
assert count == 85, count
print('PASS: 85 original attribute gem bonuses, exact 9-rank numeric values & original equipment target classes')
